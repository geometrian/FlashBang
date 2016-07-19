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

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
	private DataStream stream;

	private MeasurerAudio audio;
	private MeasurerLight light;

	private ThreadUpdate _thread_update;
	public ThreadCorrelate thread_correlate;

	public TextView text_distance;


	//Button button;
	//TextView text;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window);

		ViewGraphAudio graph_audio = (ViewGraphAudio)findViewById(R.id.graph_audio);
		ViewGraphLight graph_light = (ViewGraphLight)findViewById(R.id.graph_light);

		text_distance = (TextView)findViewById(R.id.text_distance);

		//button = (Button)this.findViewById(R.id.myButton);
		//text = (TextView)this.findViewById(R.id.textView);
		//button.setOnClickListener(this);

		//textLIGHT_available = (TextView)findViewById(R.id.LIGHT_available);
		//textLIGHT_reading   = (TextView)findViewById(R.id.LIGHT_reading);

		stream = new DataStream();

		audio = new MeasurerAudio(stream);
		audio.start();
		graph_audio.audio = audio;

		light = new MeasurerLight( stream, (SensorManager)getSystemService(SENSOR_SERVICE) );
		graph_light.light = light;

		_thread_update = new ThreadUpdate(this, stream, audio, graph_audio,graph_light);
		thread_correlate = new ThreadCorrelate(stream);

		_thread_update.start();
		thread_correlate.start();
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
