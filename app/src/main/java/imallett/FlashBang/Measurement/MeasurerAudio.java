package imallett.FlashBang.Measurement;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import imallett.FlashBang.DataStream;

public class MeasurerAudio extends MeasurerBase {
	public int SAMPLE_RATE;
	public short CHANNEL_FORMAT;
	public short SAMPLE_ENCODING;
	private AudioRecord recorder;

	private  long[] buffer_ts;
	private  byte[] buffer_b;
	private short[] buffer_s;
	private float[] buffer_f;

	private boolean createRecorder() {
		//Work around crappy APIs and lying documentation.
		//	http://stackoverflow.com/a/5440517/688624
		for (int sample_rate : new int[]{44100,32000,22050,16000,11025,8000}) {
			for (short encoding : new short[]{AudioFormat.ENCODING_PCM_FLOAT,AudioFormat.ENCODING_PCM_16BIT,AudioFormat.ENCODING_PCM_8BIT}) {
				for (short channel_format : new short[]{AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_IN_STEREO}) {
					try {
						//Log.d(C.TAG, "Attempting rate " + sample_rate + "Hz, bits: " + encoding + ", channel: " + channel_format);
						int buffer_size = AudioRecord.getMinBufferSize(sample_rate, channel_format, encoding);
						if (buffer_size!=AudioRecord.ERROR && buffer_size!=AudioRecord.ERROR_BAD_VALUE) {
							recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sample_rate, channel_format, encoding, buffer_size);
							/*recorder = new AudioRecord.Builder()
								.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
								.setAudioFormat(new AudioFormat.Builder()
									.setEncoding(encoding)
									.setSampleRate(sample_rate)
									.setChannelMask(channel_format)
									.build()
								)
								.setBufferSizeInBytes(2*buffer_size)
								.build()
							;*/
							int state = recorder.getState();
							if (state == AudioRecord.STATE_INITIALIZED) {
								SAMPLE_RATE = sample_rate;
								CHANNEL_FORMAT = channel_format;
								SAMPLE_ENCODING = encoding;
								return true;
							}
							recorder.release();
						}
					} catch (IllegalArgumentException e) {
						//Log.e(C.TAG, sample_rate + "Exception, keep trying.",e);
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	public MeasurerAudio(DataStream stream) {
		super(stream);

		if (createRecorder()) {
			assert recorder.getState()==AudioRecord.STATE_INITIALIZED;

			int buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_FORMAT, SAMPLE_ENCODING);
			assert buffer_size!=AudioRecord.ERROR && buffer_size!=AudioRecord.ERROR_BAD_VALUE;
			if (buffer_size<SAMPLE_RATE) buffer_size=SAMPLE_RATE; //Ensure it's at least one second
			buffer_ts = new  long[buffer_size];
			buffer_b  = new  byte[buffer_size];
			buffer_s  = new short[buffer_size];
			buffer_f  = new float[buffer_size];

			valid = true;
		} else {
			valid = false;
		}
	}
	@Override protected void finalize() {
		if (!valid) return;
		recorder.release();
	}

	public void start() {
		if (!valid) return;
		recorder.startRecording();
	}
	public void stop() {
		if (!valid) return;
		recorder.stop();
	}

	public void update() {
		if (!valid) return;

		int num_read;
		switch (SAMPLE_ENCODING) {
			case AudioFormat.ENCODING_PCM_FLOAT:
				num_read = recorder.read(buffer_f, 0,buffer_f.length, AudioRecord.READ_NON_BLOCKING);
				break;
			case AudioFormat.ENCODING_PCM_16BIT:
				num_read = recorder.read(buffer_s, 0,buffer_s.length, AudioRecord.READ_NON_BLOCKING);
				for (int i=0;i<buffer_s.length;++i) {
					buffer_f[i] = buffer_s[i]/32768.0f;
				}
				break;
			case AudioFormat.ENCODING_PCM_8BIT:
				num_read = recorder.read(buffer_b, 0,buffer_b.length, AudioRecord.READ_NON_BLOCKING);
				for (int i=0;i<buffer_s.length;++i) {
					buffer_f[i] = (buffer_b[i]-128.0f)/128.0f;
				}
				break;
			default:
				assert false; return;
		}
		if (num_read>0); else return;

		long tn = System.nanoTime();
		synchronized(stream) {
			int temp = -(num_read-1);
			for (int i=0;i<num_read;++i) {
				buffer_ts[i] = tn + (long)(temp + i)*(long)1000000000/(long)SAMPLE_RATE;
			}
			stream.updateVolumes(buffer_ts,buffer_f, num_read);
		}
	}
}
