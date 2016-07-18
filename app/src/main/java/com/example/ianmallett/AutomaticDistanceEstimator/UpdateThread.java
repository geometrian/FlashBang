package com.example.ianmallett.AutomaticDistanceEstimator;

import android.graphics.Canvas;
import android.view.SurfaceView;

public class UpdateThread extends Thread {
	private final MeasurerAudio _audio;
	private final ViewGraphAudio _graph_audio;
	private final ViewGraphLight _graph_light;

	public volatile boolean stop_requested = false;

	public UpdateThread(MeasurerAudio audio, ViewGraphAudio graph_audio,ViewGraphLight graph_light) {
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

			//Compute cross-correlation


			//Redraw
			_redrawSurfaceView(_graph_audio);
			_redrawSurfaceView(_graph_light);
		}
	}
}
