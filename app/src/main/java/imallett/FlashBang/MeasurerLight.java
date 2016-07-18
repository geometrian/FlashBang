package imallett.FlashBang;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MeasurerLight extends MeasurerBase {
	private float _last_lux;
	private  long _last_advances = -1;

	public MeasurerLight(DataStream stream, SensorManager sensor_manager) {
		super(stream);

		Sensor light_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (light_sensor!=null) {
			sensor_manager.registerListener(
				new SensorEventListener() {
					@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
					@Override public void onSensorChanged(SensorEvent singleton_event) {
						if (singleton_event.sensor.getType() == Sensor.TYPE_LIGHT) {
							synchronized(_stream) {
								float now_lux = singleton_event.values[0];
								_stream.updateLux( System.nanoTime(), now_lux );
								if (_last_advances!=-1 && _stream.advances-_last_advances<DataStream.N) {
									int in = DataStream.N - 1;
									int i0 = in - (int)(_stream.advances-_last_advances);
									float lux0 = _stream.buckets[i0].data[1];
									float luxn = _stream.buckets[in].data[1];
									for (int i=i0+1;i<in;++i) {
										float t = (float)(i-i0) / (float)(in-i0);
										float luxi = lux0*(1.0f-t) + luxn*t;
										_stream.buckets[i].updateLux(luxi);
									}
								}
								_last_lux = now_lux;
								_last_advances = _stream.advances;
							}
						}
					}
				},
				light_sensor,
				SensorManager.SENSOR_DELAY_FASTEST
			);

			valid = true;
		} else {
			valid = false;
		}
	}
}
