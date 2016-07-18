package com.example.ianmallett.AutomaticDistanceEstimator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class MeasurerLight extends MeasurerBase {
	public class Event { //TODO: private
		public final  long t_ns;
		public final double t_sec;
		public final double val;

		private Event(long t_ns, double val) {
			this.t_ns = t_ns;
			this.t_sec = t_ns / 1000000000.0;
			this.val = val;
		}
		@Override public String toString() { return "v="+val+" at t="+t_sec; }
	}

	public ArrayList<Event> _events = new ArrayList<Event>(); //TODO: private

	private static double lerp(double a,double b, double t) {
		return a*(1.0-t) + b*t;
	}
	private double integrateBetween(Event e0,Event e1, long x_ns_0,long x_ns_1) {
		//double m = (double)(e1.val - e0.val) / ((e1.t_ns - e0.t_ns)/1000000000.0);
		//double b = e1.val - m*e0.t_sec;
		//return 0.5*m*(x_sec_1*x_sec_1 - x_sec_0*x_sec_0) + b*(x_sec_1-x_sec_0);
		double t0 = (double)(x_ns_0 - e0.t_ns) / (double)(e1.t_ns - e0.t_ns);
		double t1 = (double)(x_ns_1 - e0.t_ns) / (double)(e1.t_ns - e0.t_ns);
		double val_l = MeasurerLight.lerp(e0.val,e1.val, t0);
		double val_r = MeasurerLight.lerp(e0.val,e1.val, t1);
		return 0.5*(val_l+val_r) * ((x_ns_1-x_ns_0)/1000000000.0);
	}
	public MeasurerLight(SensorManager sensor_manager) {
		Sensor light_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (light_sensor!=null) {
			for (int i=0;i<stat_arr.length;++i) {
				stat_arr[i] = 0.0f;
			}

			sensor_manager.registerListener(
				new SensorEventListener() {
					@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
					@Override public void onSensorChanged(SensorEvent singleton_event) {
						if (singleton_event.sensor.getType() == Sensor.TYPE_LIGHT) { synchronized(_events) {
							_ts_last_event = System.nanoTime();

							Event event = new Event( singleton_event.timestamp, singleton_event.values[0] );
							_events.add(event);
							if (_events.size()>=2) {
								//Clear samples
								for (int i=0;i<stat_arr.length;++i) {
									stat_arr[i] = 0.0f;
								}

								long t_ns_now = event.t_ns;
								long t_ns_first = t_ns_now - stat_arr.length*Config.LIGHT_SAMPLE_NS;

								//Remove unneeded events.  TODO: optimize?
								while (_events.size()>=2 && _events.get(1).t_ns<=t_ns_first) {
									_events.remove(0);
								}

								int is = 0; //sample index
								int ie = 0; //event index
								for (;ie<_events.size()-1;++ie) {
									//Trapezoid to add into array of samples
									Event e0 = _events.get(ie  );
									Event e1 = _events.get(ie+1);
									//Advance to first sample affected by the trapezoid
									while (t_ns_first+(is+1)*Config.LIGHT_SAMPLE_NS <= e0.t_ns) ++is;
									if (is>=stat_arr.length) break;
									else {
										//Add trapezoid to all affected samples
										int ist = is; //temp
										do {
											long t_ns_0 = t_ns_first + ist*Config.LIGHT_SAMPLE_NS;
											long t_ns_1 = t_ns_0 + Config.LIGHT_SAMPLE_NS;
											long max_min_ns = Math.max( t_ns_0, e0.t_ns );
											long min_max_ns = Math.min( t_ns_1, e1.t_ns );
											long overlap_ns = min_max_ns - max_min_ns;
											if (overlap_ns>0) {
												stat_arr[ist++] += (float)integrateBetween(e0,e1, max_min_ns,min_max_ns);
											} else break;
										} while (ist<stat_arr.length);
									}
								}

								//Scale samples; we want each sample to show the average lux, but the integral calculated the
								//	average lux over each second, not over each sample.
								for (int i=0;i<stat_arr.length;++i) {
									stat_arr[i] *= Config.STAT_RATE;
								}
							}
						}}
					}
				},
				light_sensor,
				SensorManager.SENSOR_DELAY_FASTEST
			);

			valid = true;
		} else {
			valid = false;
		}
	}
}
