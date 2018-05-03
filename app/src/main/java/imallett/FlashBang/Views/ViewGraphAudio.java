package imallett.FlashBang.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import imallett.FlashBang.Measurement.MeasurerAudio;

public class ViewGraphAudio extends ViewGraphBase {
	public MeasurerAudio audio;

	public ViewGraphAudio(Context context) { super(context); }
	public ViewGraphAudio(Context context, AttributeSet attrs) { super(context,attrs); }
	public ViewGraphAudio(Context context, AttributeSet attrs, int defStyleAttr) { super(context,attrs,defStyleAttr); }

	@Override public void onDraw(Canvas canvas) {
		try {
			synchronized(this) {
				super._drawBackground(canvas,audio.valid);
				super._drawAxes(canvas);
				synchronized(audio.stream) {
					super._drawGraph(canvas,audio.stream,0);
				}
			}
		} catch (NullPointerException ignored) {} //Workaround for moronic bugs in Android Studio
	}
}
