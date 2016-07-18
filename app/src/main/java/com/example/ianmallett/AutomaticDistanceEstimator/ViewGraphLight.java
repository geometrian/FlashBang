package com.example.ianmallett.AutomaticDistanceEstimator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

public class ViewGraphLight extends ViewGraphBase {
	public MeasurerLight light;

	public ViewGraphLight(Context context, AttributeSet attrs) {
		super(context,attrs); _init();
	}
	public ViewGraphLight(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr); _init();
	}
	public void _init() {
		setWillNotDraw(false);
		setWillNotCacheDrawing(true);
		getHolder().addCallback(this);
	}

	@Override public void onDraw(Canvas canvas) {
		synchronized(this) { synchronized(light._events) {
			long t_ns_now = light._events.get(light._events.size()-1).t_ns;
			long t_ns_first = t_ns_now - light.stat_arr.length*Config.LIGHT_SAMPLE_NS;
			long dt_ns = t_ns_now - t_ns_first;

			float x_shift = (float)( -(double)light.getNanosShift() / (double)dt_ns );

			super._drawPrepare(canvas,light.stat_arr);
			super._drawBackground(canvas,light.valid);
			super._drawAxes(canvas);
			super._drawGraph(canvas,light.stat_arr,x_shift);

			paint.setColor(Color.BLUE);
			double w = canvas.getWidth();
			double h = canvas.getHeight();
			for (MeasurerLight.Event e : light._events) {
				double x = (double)(e.t_ns - t_ns_first) / (double)dt_ns + x_shift;
				x *= w;
				double y = h - e.val * scale * h;
				canvas.drawCircle((float)x,(float)y, 2.0f, paint);
			}
		}}
	}
}