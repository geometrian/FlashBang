package imallett.FlashBang;

public class DataStream {
	public static final int N = Config.STAT_RATE*Config.MAX_DELAY;

	public long advances = 0;

	public static class Bucket {
		public float[] data = {
			0.0f, //max volume
			0.0f, //max lux (transformed)
			0.0f, //average humidity (relative)
			0.0f, //average pressure
			0.0f, //average temperature
			0.0f  //Cross-correlation result
		};
		public boolean[] valid = { false,false,false,false,false, false };
		public int n_h=0, n_p=0, n_t=0;

		public void updateVolume(float volume) { data[0]=Math.max(data[0],Math.abs(volume)); valid[0]=true; }
		public void updateLux(float lux) {
			data[1]=Math.max(data[1],lux); valid[1]=true;
		}
		public void updateHumidity(float humidity) {
			data[2] = data[2]*n_h + humidity;
			data[2] /= n_h++;
			valid[2] = true;
		}
		public void updatePressure(float pressure) {
			data[3] = data[2]*n_p + pressure;
			data[3] /= n_p++;
			valid[3] = true;
		}
		public void updateTemperature(float temperature) {
			data[4] = data[4]*n_t + temperature;
			data[4] /= n_t++;
			valid[4] = true;
		}

		@Override public String toString() { return "([corr], v,l,h,p,t)=(["+data[5]+"], "+data[0]+","+data[1]+","+data[2]+","+data[3]+","+data[4]+")"; }
	}

	//Maximum volume per decisecond for 10 seconds (= ~3.4029 km max distance)
	//Maximum lux per decisecond for 10 seconds (= ~3.4029 km max distance)];
	public Bucket[] buckets;
	//Ending time of last bucket, may be less than current time.  That is, bucket holds values for
	//	( t1_last-Config.BUCKET_NS, t1_last ]
	public long t1_last;

	public DataStream() {
		buckets = new Bucket[N];
		for (int i=0;i<N;++i) buckets[i]=new Bucket();
		t1_last = System.nanoTime();
	}

	public void advance() {
		System.arraycopy(buckets, 1, buckets, 0, N - 1);
		buckets[N-1] = new Bucket();
		t1_last += Config.BUCKET_NS;
		++advances;
	}

	private int _getIndex(long t) {
		while (t>=t1_last) advance();
		long dt=t1_last-t; assert dt>0;
		return N-1 - (int)(dt/Config.BUCKET_NS);
	}
	public void updateVolume(long t, float volume) {
		int i=_getIndex(t); if (i>=0) buckets[i].updateVolume(volume);
	}
	public void updateVolumes(long[] ts,float[] volumes, int n) {
		_getIndex(ts[n-1]); //advance
		long t1 = t1_last - (N-1)*Config.BUCKET_NS;
		int i=0, I=0;
		for (;i<n;++i) {
			while (ts[i]>=t1) {
				++I;
				t1 += Config.BUCKET_NS;
			}
			buckets[I].updateVolume(volumes[i]);
		}
	}
	public void updateLux(long t, float lux) {
		int i=_getIndex(t); if (i>=0) buckets[i].updateLux(lux);
	}
	public void updateHumidity(long t, float humidity) {
		int i=_getIndex(t); if (i>=0) buckets[i].updateHumidity(humidity);
	}
	public void updatePressure(long t, float pressure) {
		int i=_getIndex(t); if (i>=0) buckets[i].updatePressure(pressure);
	}
	public void updateTemperature(long t, float temperature) {
		int i=_getIndex(t); if (i>=0) buckets[i].updateTemperature(temperature);
	}
}
