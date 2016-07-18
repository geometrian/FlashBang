package imallett.FlashBang;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ViewGraphAudio extends ViewGraphBase {
	public MeasurerAudio audio;

	public ViewGraphAudio(Context context, AttributeSet attrs) {
		super(context,attrs); _init();
	}
	public ViewGraphAudio(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr); _init();
	}
	public void _init() {
		setWillNotDraw(false);
		setWillNotCacheDrawing(true);
		getHolder().addCallback(this);
	}

	@Override public void onDraw(Canvas canvas) {
		synchronized(this) {
			float x_shift = 0.0f;//audio.getNanosShift() / (double)dt_ns;

			super._drawPrepare(canvas,audio.stat_arr);
			super._drawBackground(canvas,audio.valid);
			super._drawAxes(canvas);
			super._drawGraph(canvas,audio.stat_arr,x_shift);
		}
	}


}