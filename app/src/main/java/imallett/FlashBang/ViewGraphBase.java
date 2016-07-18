package imallett.FlashBang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ViewGraphBase extends SurfaceView implements SurfaceHolder.Callback {
	protected Paint paint;
	protected float[] _xs = null;
	public float scale = 1.0f;
	public int n;

	protected ViewGraphBase(Context context, AttributeSet attrs) {
		super(context,attrs); _init();
	}
	protected ViewGraphBase(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr); _init();
	}
	private void _init() {
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	void _drawPrepare(Canvas canvas, float[] data) {
		if (_xs!=null); else {
			n = data.length;
			int w = canvas.getWidth();
			_xs = new float[n];
			for (int i=0;i<n;++i) {
				float x = (i+0.5f) / n * w;
				_xs[i] = x;
			}
		}
	}
	protected void _drawBackground(Canvas canvas, boolean valid) {
		if (valid) {
			canvas.drawARGB(255,225,255,225);
		} else {
			canvas.drawARGB(255,255,225,225);
		}
	}
	protected void _drawAxes(Canvas canvas) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		paint.setColor(Color.BLACK);
		canvas.drawLine(0,h-1, w-1,h-1, paint);
		canvas.drawLine(0,h-1, 0,0, paint);
	}
	protected void _drawGraph(Canvas canvas, float[] data, float x_shift) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		x_shift *= w;

		paint.setColor(Color.RED);
		float x0 = _xs[0];
		float y0 = h - h*scale*data[0];
		for (int i=1;i<n;++i) {
			float x1 = _xs[i] + x_shift;
			float y1 = h - h*scale*data[i];
			canvas.drawLine(x0,y0,x1,y1,paint);
			x0=x1; y0=y1;
		}
	}

	@Override public void surfaceCreated(SurfaceHolder holder) {}
	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}
	@Override public void surfaceDestroyed(SurfaceHolder holder) {}
}
