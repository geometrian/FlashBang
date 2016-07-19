package imallett.FlashBang;

public class Config {
	public static final int STAT_RATE = 10; //samples/sec; rate at which statistics are calculated and stored
	public static final long BUCKET_NS = 1000000000 / STAT_RATE;

	public static final int MAX_DELAY = 10; //data length, in seconds
}
