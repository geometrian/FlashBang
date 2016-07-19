package imallett.FlashBang.imallett.FlashBang.Threading;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.TextView;

import imallett.FlashBang.DataStream;
import imallett.FlashBang.MainActivity;
import imallett.FlashBang.R;
import imallett.FlashBang.ViewGraphAudio;
import imallett.FlashBang.ViewGraphLight;
import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerAudio;

public class ThreadUpdate extends ThreadBase {
	private final MainActivity _activity;

	private final DataStream _stream;

	private final MeasurerAudio _audio;

	private final ViewGraphAudio _graph_audio;
	private final ViewGraphLight _graph_light;

	public ThreadUpdate(MainActivity activity, DataStream stream, MeasurerAudio audio, ViewGraphAudio graph_audio, ViewGraphLight graph_light) {
		_activity = activity;
		_stream = stream;
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

			//Update calculation
			_activity.runOnUiThread(new Runnable() {
				@Override public void run() {
					long ns = _activity.thread_correlate.delay;
					double sec = ns / 1000000000.0;
					double m = sec * 343.2;
					_activity.text_distance.setText("Calculated distance: "+m);
				}
			});
		}
	}
}
