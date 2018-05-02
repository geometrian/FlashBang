package imallett.FlashBang.Activities;

import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import imallett.FlashBang.Config;
import imallett.FlashBang.DataStream;
import imallett.FlashBang.Measurement.MeasurerAudio;
import imallett.FlashBang.Measurement.MeasurerHumidity;
import imallett.FlashBang.Measurement.MeasurerLight;
import imallett.FlashBang.Measurement.MeasurerPressure;
import imallett.FlashBang.Measurement.MeasurerTemperature;
import imallett.FlashBang.R;
import imallett.FlashBang.Threading.ThreadCorrelate;
import imallett.FlashBang.Threading.ThreadUpdate;
import imallett.FlashBang.Views.ViewGraphAudio;
import imallett.FlashBang.Views.ViewGraphLight;

public class ActivityMain extends AppCompatActivity {
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
	public SeekBar seekbar_sensitivity;

	public TextView text_value_airn;
	public TextView text_value_sol;
	public TextView text_value_sos;

	public TextView text_value_dist;

	public Button button_options;
	public Button button_readme;

	@Override protected void onCreate(Bundle savedInstanceState) {
		Config.units_pressure    = Config.UNITS.values()[ getSharedPreferences(Config.units_file,MODE_PRIVATE).getInt(   "pressure", 0) ];
		Config.units_temperature = Config.UNITS.values()[ getSharedPreferences(Config.units_file,MODE_PRIVATE).getInt("temperature",11) ];
		Config.units_distance    = Config.UNITS.values()[ getSharedPreferences(Config.units_file,MODE_PRIVATE).getInt(   "distance",19) ];
		Config.units_speed       = Config.UNITS.values()[ getSharedPreferences(Config.units_file,MODE_PRIVATE).getInt(      "speed",24) ];

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window);

		ViewGraphAudio graph_audio = (ViewGraphAudio)findViewById(R.id.graph_audio);
		ViewGraphLight graph_light = (ViewGraphLight)findViewById(R.id.graph_light);

		text_value_pres  = (TextView)findViewById(R.id.text_value_pres);
		text_value_temp  = (TextView)findViewById(R.id.text_value_temp);
		text_value_RH    = (TextView)findViewById(R.id.text_value_RH);
		text_value_delay = (TextView)findViewById(R.id.text_value_delay);
		seekbar_sensitivity = (SeekBar)findViewById(R.id.seekbar_sensitivity);
		seekbar_sensitivity.setProgress(Math.round(Config.DEFAULT_SENSITIVITY*100.0f));

		text_value_airn = (TextView)findViewById(R.id.text_value_airn);
		text_value_sol  = (TextView)findViewById(R.id.text_value_sol);
		text_value_sos  = (TextView)findViewById(R.id.text_value_sos);

		text_value_dist = (TextView)findViewById(R.id.text_value_dist);

		button_options = (Button)findViewById(R.id.button_options);
		button_options.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(ActivityMain.this, ActivityOptions.class);
				ActivityMain.this.startActivity(intent);
			}
		});
		button_readme = (Button)findViewById(R.id.button_readme);
		button_readme.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(ActivityMain.this, ActivityReadme.class);
				ActivityMain.this.startActivity(intent);
			}
		});

		DataStream stream = new DataStream();

		SensorManager sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);

		audio = new MeasurerAudio(stream);
		graph_audio.audio = audio;
		audio.start();

		light = new MeasurerLight(stream,sensor_manager);
		graph_light.light = light;

		humidity = new MeasurerHumidity(stream, sensor_manager);

		pressure = new MeasurerPressure(stream, sensor_manager);

		temperature = new MeasurerTemperature(stream, sensor_manager);

		_thread_update = new ThreadUpdate(this, audio, graph_audio,graph_light);
		thread_correlate = new ThreadCorrelate(this, stream);

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
