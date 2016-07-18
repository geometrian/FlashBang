package imallett.FlashBang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

public class ViewGraphLight extends ViewGraphBase {
	public MeasurerLight light;

	public ViewGraphLight(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	public ViewGraphLight(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

	@Override public void onDraw(Canvas canvas) {
		super._drawPrepare(canvas);
		synchronized(this) {
			super._drawBackground(canvas,light.valid);
			super._drawAxes(canvas);
			synchronized(light._stream) {
				super._drawGraph(canvas,light._stream,1);
			}
		}
	}
}