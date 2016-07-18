package imallett.FlashBang;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ViewGraphAudio extends ViewGraphBase {
	public MeasurerAudio audio;

	public ViewGraphAudio(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	public ViewGraphAudio(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

	@Override public void onDraw(Canvas canvas) {
		super._drawPrepare(canvas);
		synchronized(this) {
			super._drawBackground(canvas,audio.valid);
			super._drawAxes(canvas);
			synchronized(audio._stream) {
				super._drawGraph(canvas,audio._stream,0);
			}
		}
	}


}