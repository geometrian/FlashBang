package com.example.ianmallett.AutomaticDistanceEstimator;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
	MeasurerAudio audio;
	MeasurerLight light;

	UpdateThread update_thread;

	//TextView textLIGHT_available, textLIGHT_reading;

	//Button button;
	//TextView text;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_window);

		ViewGraphAudio graph_audio = (ViewGraphAudio)findViewById(R.id.graph_audio);
		ViewGraphLight graph_light = (ViewGraphLight)findViewById(R.id.graph_light);

		//button = (Button)this.findViewById(R.id.myButton);
		//text = (TextView)this.findViewById(R.id.textView);
		//button.setOnClickListener(this);

		//textLIGHT_available = (TextView)findViewById(R.id.LIGHT_available);
		//textLIGHT_reading   = (TextView)findViewById(R.id.LIGHT_reading);

		audio = new MeasurerAudio();
		audio.start();
		graph_audio.audio = audio;

		light = new MeasurerLight( (SensorManager)getSystemService(SENSOR_SERVICE) );
		graph_light.light = light;
		graph_light.scale = 1.0f/500.0f;

		update_thread = new UpdateThread(audio, graph_audio,graph_light);
		update_thread.start();
	}
	@Override protected void onDestroy() {
		update_thread.stop_requested = true;
		boolean retry = true;
		do {
			try {
				update_thread.join();
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
