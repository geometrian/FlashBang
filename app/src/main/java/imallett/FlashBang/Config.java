package imallett.FlashBang;

public class Config {
	public static final int STAT_RATE = 50; //samples/sec; rate at which statistics are calculated and stored
	public static final long BUCKET_NS = 1000000000 / STAT_RATE;

	public static final int MAX_DELAY = 3; //data length, in seconds
}
