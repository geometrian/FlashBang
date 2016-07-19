package imallett.FlashBang.imallett.FlashBang.Threading;

import imallett.FlashBang.Config;
import imallett.FlashBang.DataStream;

public class ThreadCorrelate extends ThreadBase {
	private final DataStream _stream;
	private float[] _xs = new float[DataStream.N];
	private float[] _ys = new float[DataStream.N];
	private float[] _rs = new float[DataStream.N];
	public volatile long delay;

	public ThreadCorrelate(DataStream stream) {
		_stream = stream;
	}

	private void _nonnegativeDelayCrossCorrelation(int step) {
		//Compute cross-correlation for non-negative delays
		//	http://paulbourke.net/miscellaneous/correlate/
		//	Subtract off means
		float mx=0.0f, my=0.0f;
		for (int i=0;i<DataStream.N;++i) {
			mx+=_xs[i]; my+=_ys[i];
		}
		mx/=DataStream.N; my/=DataStream.N;
		for (int i=0;i<DataStream.N;++i) {
			_xs[i]-=mx; _ys[i]-=my;
		}
		//	Compute denominator
		float sx=0.0f, sy=0.0f;
		for (int i=0;i<DataStream.N;++i) {
			sx+=_xs[i]*_xs[i]; sy+=_ys[i]*_ys[i];
		}
		float recip_denom = (float)(1.0/Math.sqrt(sx*sy));
		//	Do cross-correlation
		for (int delay=0;delay<DataStream.N;delay+=step) {
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
			float r = sxy * recip_denom;
			for (int i=delay;i<DataStream.N&&i<delay+step;++i) {
				_rs[i] = r;
			}
		}
	}

	@Override public void run() {
		while (!stop_requested) {
			for (int i=0;i<DataStream.N;++i) {
				_ys[i] = _stream.buckets[i].data[0];
				_xs[i] = _stream.buckets[i].data[1];
			}
			_nonnegativeDelayCrossCorrelation(1);
			float best_r = -1.0f;
			int best_i = -1;
			for (int i=0;i<DataStream.N;++i) {
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
			delay = best_i*Config.BUCKET_NS;
		}
	}
}
