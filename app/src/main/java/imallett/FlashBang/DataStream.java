package imallett.FlashBang;

import android.util.Log;

public class DataStream {
	public static final int N = Config.STAT_RATE*Config.MAX_DELAY;

	public long advances = 0;

	public static class Bucket {
		public float[] data = {
			0.0f, //max volume
			0.0f, //max lux
			0.0f, //average humidity
			0.0f  //average temperature
		};
		public boolean[] valid = { false,false,false,false };
		public int n_h=0, n_t=0;

		public void updateVolume(float volume) { data[0]=Math.max(data[0],Math.abs(volume)); valid[0]=true; }
		public void updateLux(float lux) {
			data[1]=Math.max(data[1],lux); valid[1]=true;
		}
		public void updateHumidity(float humidity) {
			data[2] = data[2]*n_h + humidity;
			data[2] /= n_h++;
			valid[2] = true;
		}
		public void updateTemperature(float temperature) {
			data[3] = data[3]*n_t + temperature;
			data[3] /= n_t++;
			valid[3] = true;
		}

		/*public float getMaxVolume() { return data[0]; }
		public float getMaxLux() { return data[1]; }
		public float getAvgHumidity() { return data[2]; }
		public float getAvgTemperature() { return data[3]; }*/

		@Override public String toString() { return "(v,l,h,t)=("+data[0]+","+data[1]+","+data[2]+","+data[3]+")"; }
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
		for (int i=0;i<N-1;++i) {
			buckets[i] = buckets[i+1];
		}
		buckets[N-1] = new Bucket();
		t1_last += Config.BUCKET_NS;
		++advances;
	}

	private int _getIndex(long t) {
		while (t>=t1_last) advance();
		long dt=t1_last-t; assert dt>0;
		int i = N-1 - (int)(dt/Config.BUCKET_NS);
		return i;
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
	public void updateTemperature(long t, float temperature) {
		int i=_getIndex(t); if (i>=0) buckets[i].updateTemperature(temperature);
	}
}
