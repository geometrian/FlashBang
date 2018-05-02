package imallett.FlashBang.Threading;

import android.graphics.Canvas;
import android.view.SurfaceView;

import imallett.FlashBang.Config;
import imallett.FlashBang.Activities.ActivityMain;
import imallett.FlashBang.R;
import imallett.FlashBang.Views.ViewGraphAudio;
import imallett.FlashBang.Views.ViewGraphLight;
import imallett.FlashBang.Measurement.MeasurerAudio;

public class ThreadUpdate extends ThreadBase {
	private final ActivityMain _activity;

	private final MeasurerAudio _audio;

	private final ViewGraphAudio _graph_audio;
	private final ViewGraphLight _graph_light;

	public ThreadUpdate(ActivityMain activity, MeasurerAudio audio, ViewGraphAudio graph_audio, ViewGraphLight graph_light) {
		_activity = activity;
		_audio = audio;
		_graph_audio = graph_audio;
		_graph_light = graph_light;
	}

	private void _redrawSurfaceView(SurfaceView surface) {
		//Note: evidently checking _graphs.getHolder().getSurface().isValid() is insufficient.
		Canvas canvas = surface.getHolder().lockCanvas(null);
		if (canvas != null) {
			surface.draw(canvas);
			surface.getHolder().unlockCanvasAndPost(canvas);
			surface.postInvalidate();
		}
	}
	@Override public void run() {
		while (!stop_requested) {
			//Update data
			_audio.update();
			//	lux updates via a callback automatically

			//Redraw
			_redrawSurfaceView(_graph_audio);
			_redrawSurfaceView(_graph_light);

			//Update GUI
			_activity.runOnUiThread(new Runnable() { @Override public void run() {
				//Input values
				//	Relative humidity (%)
				double RH = _activity.humidity.valid ? _activity.humidity.RH : 45.0;
				//	Pressure (Pa)
				double P = _activity.pressure.valid ? _activity.pressure.hPa*100.0 : 101325.0;
				//	Temperature (°C)
				double T = _activity.temperature.valid ? _activity.temperature.degC : 20.0;
				//	Delay (sec)
				long ns = _activity.thread_correlate.delay;
				double delay = ns / 1000000000.0;

				//Calculate refractive index
				//	http://physics.stackexchange.com/a/14948/10909
				//	TODO: make dependent on humidity.  See https://www.mathworks.com/matlabcentral/fileexchange/31240-air-refractive-index/content/air_index.m
				double P0 = 101325.0; //in Pa
				double T0 = 300.0 - 273.15; //in deg C
				double n = 1.0 + 0.000293 * P/P0 * T0/T;

				//Calculate speed of light in air
				//	n=c/v -> v=c/n
				double c = 299792458.0;
				double v = c / n;

				//Calculate speed of sound in air
				//	http://www.sengpielaudio.com/calculator-airpressure.htm
				double T_kelvin = T + 273.15;
				//	Molecular concentration of water vapor calculated from Rh using Giacomos method by Davis (1991) as implemented in DTU report 11b-1997
				double ENH = 1.00062 + (Math.PI*1.0e-8)*P + 5.6e-7*T*T;

				//	Mole fraction of carbon dioxide
				double Xc = 3.14e-4; //Version had this commented out and replaced by "4.0".  Don't know why.
				//	Mole fraction of water vapor
				//		The commented values correspond to values used in Cramer (Appendix)
				//            1.2811805e-5                   - 1.9509874e-2
				double PSV1 = 1.2378847e-5*T_kelvin*T_kelvin - 1.9121316e-2*T_kelvin;
				//            34.04926034 - 6.3536311e3
				double PSV2 = 33.93711047 - 6.3431645e3/T_kelvin;
				double PSV = Math.exp(PSV1 + PSV2);
				double H = RH*ENH*PSV/P; //Molarity of water vapor
				double Xw = 0.01 * H;
				//	Calculate speed using the method of Cramer from JASA vol 93 p. 2510
				double C1 = 331.5024 + 0.603055*T - 5.28e-4*T*T + (51.471935 + 0.1495874*T - 7.82e-4*T*T)*Xw;
				double C2 = (-1.82e-7 + 3.73e-8*T - 2.93e-10*T*T)*P + (-85.20931 - 0.228525*T + 5.91e-5*T*T)*Xc;
				double C3 = 2.835149*Xw*Xw - 2.15e-13*P*P + 29.179762*Xc*Xc + 4.86e-4*Xw*P*Xc;
				double C = C1 + C2 - C3;

				//Calculate distance
				//	d=r_1*t_1=r_2*t_2 -> d=r_1*r_2*((t_2-t_1)/(r_1-r_2))
				double dist = v*C*( delay / (v-C) );

				//Update text
				//	Simple inputs
				_activity.text_value_pres. setText(_activity.getString(R.string.caption_input_press)+" "+Config.localizePressure(P));
				_activity.text_value_temp. setText(_activity.getString(R.string.caption_input_temp)+" "+Config.localizeTemperature(T));
				_activity.text_value_RH.   setText(_activity.getString(R.string.caption_input_RH)+" "+RH+"%");
				if (_activity.audio.valid&&_activity.light.valid) {
					if (ns!=-1) {
						_activity.text_value_delay.setText(_activity.getString(R.string.caption_input_okdelay)+" "+String.format("%.2f",delay)+" sec");
					} else {
						_activity.text_value_delay.setText(_activity.getString(R.string.caption_input_abdelay));
					}
				} else {
					_activity.text_value_delay.setText(_activity.getString(R.string.caption_input_nodelay));
				}
				//	Derived quantities
				_activity.text_value_airn.setText(_activity.getString(R.string.caption_derived_n)+" "+String.format("%.9f",n));
				_activity.text_value_sol. setText(_activity.getString(R.string.caption_derived_sol)+" "+Config.localizeSpeed(v));
				_activity.text_value_sos. setText(_activity.getString(R.string.caption_derived_sos)+" "+Config.localizeSpeed(C));
				//	Value
				if (_activity.audio.valid&&_activity.light.valid) {
					if (ns!=-1) {
						_activity.text_value_dist.setText(_activity.getString(R.string.caption_dist_okval)+" "+Config.localizeLength(dist));
					} else {
						_activity.text_value_dist.setText(_activity.getString(R.string.caption_dist_abval));
					}
				} else {
					_activity.text_value_dist.setText(_activity.getString(R.string.caption_dist_noval));
				}
			}});
		}
	}
}
