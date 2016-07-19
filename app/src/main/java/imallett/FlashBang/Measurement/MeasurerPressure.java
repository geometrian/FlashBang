package imallett.FlashBang.Measurement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import imallett.FlashBang.DataStream;

public class MeasurerPressure extends MeasurerBase {
	public float hPa;

	public MeasurerPressure(DataStream stream, SensorManager sensor_manager) {
		super(stream);

		final Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		if (sensor!=null) {
			sensor_manager.registerListener(
				new SensorEventListener() {
					@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
					@Override public void onSensorChanged(SensorEvent singleton_event) {
						if (singleton_event.sensor.getType() == Sensor.TYPE_PRESSURE) {
							hPa = singleton_event.values[0];
						}
					}
				},
				sensor,
				SensorManager.SENSOR_DELAY_FASTEST
			);
			valid = true;
		} else {
			valid = false;
		}
	}
}
