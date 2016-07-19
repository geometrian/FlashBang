package imallett.FlashBang.imallett.FlashBang.Threading;

import imallett.FlashBang.Config;
import imallett.FlashBang.DataStream;

public class ThreadCorrelate extends ThreadBase {
	private final DataStream _stream;

	private float[] _xs = new float[DataStream.N];
	private float[] _ys = new float[DataStream.N];
	private float[] _rs = new float[DataStream.N];
	private float _recip_denom;

	public volatile long delay = -1;

	public ThreadCorrelate(DataStream stream) {
		_stream = stream;
	}

	private void _subtractMeansCalcDenom() {
		//Subtract off means
		float mx=0.0f, my=0.0f;
		for (int i=0;i<DataStream.N;++i) {
			mx+=_xs[i]; my+=_ys[i];
		}
		mx/=DataStream.N; my/=DataStream.N;
		for (int i=0;i<DataStream.N;++i) {
			_xs[i]-=mx; _ys[i]-=my;
		}

		//Compute denominator
		float sx=0.0f, sy=0.0f;
		for (int i=0;i<DataStream.N;++i) {
			sx+=_xs[i]*_xs[i]; sy+=_ys[i]*_ys[i];
		}
		_recip_denom = (float)(1.0/Math.sqrt(sx*sy));
	}
	private void _nonnegativeDelayCrossCorrelation(int start,int end,int step) {
		//Compute cross-correlation for non-negative delays
		//	http://paulbourke.net/miscellaneous/correlate/
		//	Do cross-correlation
		for (int delay=start;delay<end;delay+=step) {
			float sxy = 0.0f;
			for (int i=0;i<DataStream.N;++i) {
				int j = i + delay;
				float d0 = _xs[i];
				float d1;
				if (j>=DataStream.N) { //if (j<0 || j>=DataStream.N) {
					continue;
				} else {
					d1 = _ys[j];
				}
				sxy += d0*d1;
			}
			float r = sxy * _recip_denom;
			for (int i=delay;i<DataStream.N&&i<delay+step;++i) {
				_rs[i] = r;
			}
		}
	}
	private int _getBestCorrelation(int start,int stop,int step) {
		float best_r = Float.NEGATIVE_INFINITY;
		int best_i = -1;
		for (int i=start;i<stop;i+=step) {
			if (_stream.buckets[i].valid[0]&&_stream.buckets[i].valid[1]) {
				_stream.buckets[i].data[5] = _rs[i];
				_stream.buckets[i].valid[5] = true;
				if (_rs[i]>best_r) {
					best_i = i;
					best_r = _rs[i];
				}
			} else {
				_stream.buckets[i].data[5] = 0.0f;
				_stream.buckets[i].valid[5] = false;
			}
		}
		return best_i;
	}
	private int _doCorrelationPhase(int start,int stop,int step) {
		_nonnegativeDelayCrossCorrelation(start,stop,step);
		return _getBestCorrelation(start,stop,step);
	}

	@Override public void run() {
		while (!stop_requested) {
			for (int i=0;i<DataStream.N;++i) {
				_ys[i] = _stream.buckets[i].data[0];
				_xs[i] = _stream.buckets[i].data[1];
			}
			_subtractMeansCalcDenom();

			int best_i;
			best_i = _doCorrelationPhase(0,DataStream.N,10);
			if (best_i!=-1); else continue;
			best_i = _doCorrelationPhase(best_i,Math.min(best_i+10,DataStream.N),1);
			if (best_i!=-1); else continue;

			delay = best_i*Config.BUCKET_NS;
		}
	}
}
