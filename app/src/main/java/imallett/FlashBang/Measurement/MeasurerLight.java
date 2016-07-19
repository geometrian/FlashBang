package imallett.FlashBang.Measurement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import imallett.FlashBang.DataStream;

public class MeasurerLight extends MeasurerBase {
	private float _last_lux;
	private  long _last_advances = -1;

	private void _init(SensorManager sensor_manager) {
		final Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (sensor!=null) {
			sensor_manager.registerListener(
				new SensorEventListener() {
					@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
					@Override public void onSensorChanged(SensorEvent singleton_event) {
						if (singleton_event.sensor.getType() == Sensor.TYPE_LIGHT) {
							synchronized(stream) {
								float temp = singleton_event.values[0];
								float now_lux;
								if (temp>0.0f) now_lux=(float)( Math.log(temp) / Math.log(sensor.getMaximumRange()) );
								else           now_lux=0.0f;

								stream.updateLux( System.nanoTime(), now_lux );
								if (_last_advances!=-1 && stream.advances-_last_advances<DataStream.N) {
									int in = DataStream.N - 1;
									int i0 = in - (int)(stream.advances-_last_advances);
									float lux0 = stream.buckets[i0].data[1];
									float luxn = stream.buckets[in].data[1];
									for (int i=i0+1;i<in;++i) {
										float t = (float)(i-i0) / (float)(in-i0);
										float luxi = lux0*(1.0f-t) + luxn*t;
										stream.buckets[i].updateLux(luxi);
									}
								}
								_last_lux = now_lux;
								_last_advances = stream.advances;
							}
						}
					}
				},
				sensor,
				SensorManager.SENSOR_DELAY_FASTEST
			);

			valid = true;
		} else {
			valid = false;
		}
	}
	public MeasurerLight(DataStream stream, SensorManager sensor_manager) {
		super(stream);
		_init(sensor_manager);
	}
}
