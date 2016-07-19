package imallett.FlashBang;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerAudio;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerHumidity;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerLight;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerPressure;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerTemperature;
import imallett.FlashBang.imallett.FlashBang.Threading.ThreadCorrelate;
import imallett.FlashBang.imallett.FlashBang.Threading.ThreadUpdate;
import imallett.FlashBang.imallett.FlashBang.Views.ViewGraphAudio;
import imallett.FlashBang.imallett.FlashBang.Views.ViewGraphLight;

public class MainActivity extends AppCompatActivity {
	private DataStream stream;

	public MeasurerAudio audio;
	public MeasurerLight light;
	public MeasurerHumidity humidity;
	public MeasurerPressure pressure;
	public MeasurerTemperature temperature;

	private ThreadUpdate _thread_update;
	public ThreadCorrelate thread_correlate;

	public TextView text_value_pres;
	public TextView text_value_temp;
	public TextView text_value_RH;
	public TextView text_value_delay;

	public TextView text_value_airn;
	public TextView text_value_sol;
	public TextView text_value_sos;

	public TextView text_value_dist;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window);

		ViewGraphAudio graph_audio = (ViewGraphAudio)findViewById(R.id.graph_audio);
		ViewGraphLight graph_light = (ViewGraphLight)findViewById(R.id.graph_light);

		text_value_pres  = (TextView)findViewById(R.id.text_value_pres);
		text_value_temp  = (TextView)findViewById(R.id.text_value_temp);
		text_value_RH    = (TextView)findViewById(R.id.text_value_RH);
		text_value_delay = (TextView)findViewById(R.id.text_value_delay);

		text_value_airn = (TextView)findViewById(R.id.text_value_airn);
		text_value_sol  = (TextView)findViewById(R.id.text_value_sol);
		text_value_sos  = (TextView)findViewById(R.id.text_value_sos);

		text_value_dist = (TextView)findViewById(R.id.text_value_dist);

		stream = new DataStream();

		SensorManager sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);

		audio = new MeasurerAudio(stream);
		graph_audio.audio = audio;
		audio.start();

		light = new MeasurerLight(stream,sensor_manager);
		graph_light.light = light;

		humidity = new MeasurerHumidity(stream, sensor_manager);

		pressure = new MeasurerPressure(stream, sensor_manager);

		temperature = new MeasurerTemperature(stream, sensor_manager);

		_thread_update = new ThreadUpdate(this, stream, audio, graph_audio,graph_light);
		thread_correlate = new ThreadCorrelate(stream);

		_thread_update.start();
		thread_correlate.start();

		if (humidity.valid) text_value_RH.setBackgroundColor(getColor(R.color.color_good));
		if (pressure.valid) text_value_pres.setBackgroundColor(getColor(R.color.color_good));
		if (temperature.valid) text_value_temp.setBackgroundColor(getColor(R.color.color_good));
		if (audio.valid && light.valid) {
			text_value_delay.setBackgroundColor(getColor(R.color.color_good));
			text_value_dist.setBackgroundColor(getColor(R.color.color_good));
		}
	}
	@Override protected void onDestroy() {
		_thread_update.stop_requested = true;
		thread_correlate.stop_requested = true;
		boolean retry = true;
		do {
			try {
				_thread_update.join();
				thread_correlate.join();
				retry = false;
			} catch (InterruptedException e) {}
		} while (retry);

		audio.stop();

		super.onDestroy();
	}
}
