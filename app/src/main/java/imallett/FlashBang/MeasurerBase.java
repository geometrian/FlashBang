package imallett.FlashBang;

public class MeasurerBase {
	public boolean valid;

	protected long _ts_last_event = -1;

	//Maximum volume per decisecond for 10 seconds (= ~3.4029 km max distance)
	//Maximum lux per decisecond for 10 seconds (= ~3.4029 km max distance)];
	public float[] stat_arr = new float[Config.STAT_RATE*Config.MAX_DELAY];

	public long getNanosShift() {
		if (_ts_last_event!=-1) {
			return System.nanoTime() - _ts_last_event;
		} else {
			return 0;
		}
	}
}
