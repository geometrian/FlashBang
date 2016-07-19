package imallett.FlashBang.imallett.FlashBang.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import imallett.FlashBang.Config;
import imallett.FlashBang.DataStream;
import imallett.FlashBang.R;

public class ViewGraphBase extends SurfaceView implements SurfaceHolder.Callback {
	protected Paint paint;

	private class Point {
		float x, y;
		@Override public String toString() { return "(x,y)=("+x+","+y+")"; }
	}
	private Point[] _points = new Point[DataStream.N];

	public float scale = 1.0f;

	protected ViewGraphBase(Context context) {
		super(context); _init();
	}
	protected ViewGraphBase(Context context, AttributeSet attrs) {
		super(context,attrs); _init();
	}
	protected ViewGraphBase(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr); _init();
	}
	private void _init() {
		setWillNotDraw(false);
		setWillNotCacheDrawing(true);
		getHolder().addCallback(this);

		for (int i=0;i<DataStream.N;++i) _points[i]=new Point();

		paint = new Paint();
		paint.setAntiAlias(true);
	}

	void _drawPrepare(Canvas canvas) {
		/*if (_xs!=null); else {
			int w = canvas.getWidth();
			for (int i=0;i<DataStream.N;++i) {
				float x = (i+0.5f) / DataStream.N * w;
				_xs[i] = x;
			}
		}*/
	}
	protected void _drawBackground(Canvas canvas, boolean valid) {
		if (valid) {
			canvas.drawColor( ContextCompat.getColor(getContext(), R.color.color_good) );
		} else {
			canvas.drawColor( ContextCompat.getColor(getContext(), R.color.color_bad) );
		}
	}
	protected void _drawAxes(Canvas canvas) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		paint.setColor(Color.BLACK);
		canvas.drawLine(0,h-6, w-1,h-6, paint);
		canvas.drawLine(w-1,h-1, w-1,0, paint);
	}
	protected void _drawGraph(Canvas canvas, DataStream data,int attrib_index) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		long tn = data.t1_last;
		long t0 = tn - (DataStream.N-1)* Config.BUCKET_NS;
		long tr = System.nanoTime();
		long tl = tr - (DataStream.N-1)*Config.BUCKET_NS;
		long t;
		float sc = (h-5)*scale;
		for (int i=0;i<DataStream.N;++i) {
			t = t0 + i*Config.BUCKET_NS;
			float part = (float)( (double)(t-tl) / (double)(tr-tl) );
			_points[i].x = part*w;
			_points[i].y = h-6 - sc*data.buckets[i].data[attrib_index];
		}

		paint.setColor(Color.RED);
		boolean prev_okay = data.buckets[0].valid[attrib_index];
		float x0 = _points[0].x;
		float y0 = _points[0].y;
		for (int i=1;i<DataStream.N;++i) {
			float x1=_points[i].x, y1=_points[i].y;
			boolean this_okay = data.buckets[i].valid[attrib_index];
			if (prev_okay && this_okay) {
				canvas.drawLine( x0,y0, x1,y1, paint );
			}
			prev_okay=this_okay; x0=x1; y0=y1;
		}
	}

	@Override public void surfaceCreated(SurfaceHolder holder) {}
	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}
	@Override public void surfaceDestroyed(SurfaceHolder holder) {}
}
