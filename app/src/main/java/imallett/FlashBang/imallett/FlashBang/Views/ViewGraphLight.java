package imallett.FlashBang.imallett.FlashBang.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import imallett.FlashBang.imallett.FlashBang.Measurement.MeasurerLight;

public class ViewGraphLight extends ViewGraphBase {
	public MeasurerLight light;

	public ViewGraphLight(Context context) { super(context); }
	public ViewGraphLight(Context context, AttributeSet attrs) { super(context,attrs); }
	public ViewGraphLight(Context context, AttributeSet attrs, int defStyleAttr) { super(context,attrs,defStyleAttr); }

	@Override public void onDraw(Canvas canvas) {
		try {
			synchronized(this) {
				super._drawBackground(canvas,light.valid);
				super._drawAxes(canvas);
				synchronized(light.stream) {
					super._drawGraph(canvas,light.stream,1);
					//super._drawGraph(canvas,light.stream,5);
				}
			}
		} catch (NullPointerException e) {} //Workaround for moronic bugs in Android Studio
	}
}