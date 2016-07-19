package imallett.FlashBang;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerAudio;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerLight;
import imallett.FlashBang.imallett.FlashBang.Threading.ThreadCorrelate;
import imallett.FlashBang.imallett.FlashBang.Threading.ThreadUpdate;
import imallett.FlashBang.imallett.FlashBang.Views.ViewGraphAudio;
import imallett.FlashBang.imallett.FlashBang.Views.ViewGraphLight;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
	private DataStream stream;

	private MeasurerAudio audio;
	private MeasurerLight light;

	private ThreadUpdate _thread_update;
	public ThreadCorrelate thread_correlate;

	public TextView text_factor_sound;
	public TextView text_factor_sol;
	public TextView text_factor_humidity;
	public TextView text_factor_pressure;
	public TextView text_factor_temperature;
	public TextView text_distance;


	//Button button;
	//TextView text;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window);

		ViewGraphAudio graph_audio = (ViewGraphAudio)findViewById(R.id.graph_audio);
		ViewGraphLight graph_light = (ViewGraphLight)findViewById(R.id.graph_light);

		text_factor_sound        = (TextView)findViewById(R.id.text_sound);
		text_factor_sol          = (TextView)findViewById(R.id.text_sol);
		text_factor_humidity     = (TextView)findViewById(R.id.text_humidity);
		text_factor_pressure     = (TextView)findViewById(R.id.text_pressure);
		text_factor_temperature  = (TextView)findViewById(R.id.text_temperature);
		text_distance            = (TextView)findViewById(R.id.text_distance);

		//button = (Button)this.findViewById(R.id.myButton);
		//text = (TextView)this.findViewById(R.id.textView);
		//button.setOnClickListener(this);

		//textLIGHT_available = (TextView)findViewById(R.id.LIGHT_available);
		//textLIGHT_reading   = (TextView)findViewById(R.id.LIGHT_reading);

		stream = new DataStream();

		audio = new MeasurerAudio(stream);
		graph_audio.audio = audio;

		light = new MeasurerLight( stream, (SensorManager)getSystemService(SENSOR_SERVICE) );
		graph_light.light = light;

		_thread_update = new ThreadUpdate(this, stream, audio, graph_audio,graph_light);
		thread_correlate = new ThreadCorrelate(stream);

		_thread_update.start();
		thread_correlate.start();

		if (audio.valid) {
			audio.start();
			text_factor_sound.setText("  "+getString(R.string.factor_ok_sound));
			text_factor_sound.setBackgroundColor(getColor(R.color.color_good));
		}
		if (light.valid) {
			text_factor_sol.setText("  "+getString(R.string.factor_ok_sol));
			text_factor_sol.setBackgroundColor(getColor(R.color.color_good));
		}
		if (audio.valid&&light.valid) {
			text_distance.setBackgroundColor(getColor(R.color.color_good));
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

	@Override public void onClick(View view) {
			//text.setText("You pressed the button!");
	}
}
