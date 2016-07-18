package imallett.FlashBang;

public abstract class MeasurerBase {
	public boolean valid;

	protected DataStream _stream;

	protected MeasurerBase(DataStream stream) {
		_stream = stream;
	}
}
