package imallett.FlashBang;

public class Config {
	public static final int STAT_RATE = 100; //samples/sec; rate at which statistics are calculated and stored
	public static final long BUCKET_NS = 1000000000 / STAT_RATE;

	public static final int MAX_DELAY = 10; //data length, in seconds

	public static final float DEFAULT_SENSITIVITY = 0.7f;

	public static String localizePressure(double Pa) {
		return String.format("%.4f kPa",Pa/1000.0);
	}
	public static String localizeTemperature(double degC) {
		return String.format("%.2f°C",degC);
	}
	public static String localizeLength(double m) {
		return String.format("%.3f m",m);
	}
	public static String localizeSpeed(double m_per_sec) {
		return String.format("%.3f m/s",m_per_sec);
	}
}
