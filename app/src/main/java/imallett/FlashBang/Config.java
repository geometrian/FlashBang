package imallett.FlashBang;

public class Config {
	public static final int STAT_RATE = 100; //samples/sec; rate at which statistics are calculated and stored
	public static final long BUCKET_NS = 1000000000 / STAT_RATE;

	public static final int MAX_DELAY = 10; //data length, in seconds

	public static final float DEFAULT_SENSITIVITY = 0.7f;

	public enum UNITS {
		Pa,kPa,hPa,psi,bar,dbar,mbar,Ba,atm,Torr,mTorr,
		C,K,F,R,De,N,Re,Ro,
		m,km,ft,yd,mi,
		m_per_sec,km_per_sec,km_per_hr,ft_per_sec,mi_per_hr,knots
	}
	public static UNITS units_pressure;
	public static UNITS units_temperature;
	public static UNITS units_distance;
	public static UNITS units_speed;

	public static String units_file = "unit_prefs";

	public static String localizePressure(double Pa) {
		switch (units_pressure) {
			case Pa:  return String.format("%.1f Pa",Pa);
			case kPa: return String.format("%.4f kPa",Pa*1.0e-3);
			case hPa: return String.format("%.3f hPa",Pa*1.0e-2);
			case psi: return String.format("%.3f psi",Pa*1.450377e-4);
			case bar:  return String.format("%.6f bar",Pa*1.0e-5);
			case dbar: return String.format("%.5f dbar",Pa*1.0e-4);
			case mbar: return String.format("%.3f mbar",Pa*1.0e-2);
			case Ba: return String.format("%d Ba",Math.round(Pa*10.0));
			case atm: return String.format("%.4f atm",Pa*(1.0/101325.0));
			case Torr: return String.format("%.4f Torr",Pa*(760.0/101325.0));
			case mTorr: return String.format("%.1f mTorr",Pa*(760000.0/101325.0));
			default: assert false; return null;
		}
	}
	public static String localizeTemperature(double degC) {
		switch (units_temperature) {
			//https://en.wikipedia.org/wiki/Temperature#Conversion
			case C: return String.format("%.2f°C",degC);
			case K: return String.format("%.2f°K",degC+273.15);
			case F: return String.format("%.2f°F",degC*1.8+32.0);
			case R: return String.format("%.2f°R",(degC+273.15)*1.8);
			case De: return String.format("%.2f°De",(100.0-degC)*1.5);
			case N: return String.format("%.3f°N",degC*0.33); //Yes, this is exact.
			case Re: return String.format("%.3f°Ré",degC*0.8);
			case Ro: return String.format("%.3f°Rø",degC*0.525+7.5);
			default: assert false; return null;
		}
	}
	public static String localizeLength(double m) {
		switch (units_distance) {
			case m: return String.format("%.3f m",m);
			case km: return String.format("%.6f km",m*0.001);
			case ft: return String.format("%.2f ft",m*(100.0/30.48));
			case yd: return String.format("%.3f yd",m*(100/91.44));
			case mi: return String.format("%.6f mi",m*(100/160934.4));
			default: assert false; return null;
		}
	}
	public static String localizeSpeed(double m_per_sec) {
		switch (units_speed) {
			case m_per_sec: return String.format("%.3f m/s",m_per_sec);
			case km_per_sec: return String.format("%.6f km/s",m_per_sec*0.001);
			case km_per_hr: return String.format("%.3f kph",m_per_sec*3.6);
			case ft_per_sec: return String.format("%.3f fps",m_per_sec*(100.0/30.48));
			case mi_per_hr: return String.format("%.3f mph",m_per_sec*(360000.0/160934.4));
			case knots: return String.format("%.3f kn",m_per_sec*(3600.0/1852.0));
			default: assert false; return null;
		}
	}
}
