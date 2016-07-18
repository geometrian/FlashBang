package imallett.FlashBang;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MeasurerAudio extends MeasurerBase {
	public int SAMPLE_RATE;
	public short CHANNEL_FORMAT;
	public short SAMPLE_ENCODING;
	private AudioRecord recorder;

	private int offset = 0;
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
							if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
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
	public MeasurerAudio() {
		if (createRecorder()) {
			assert recorder.getState()==AudioRecord.STATE_INITIALIZED;

			int buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_FORMAT, SAMPLE_ENCODING);
			assert buffer_size!=AudioRecord.ERROR && buffer_size!=AudioRecord.ERROR_BAD_VALUE;
			if (buffer_size<SAMPLE_RATE) buffer_size=SAMPLE_RATE; //Ensure it's at least one second
			buffer_b = new  byte[buffer_size];
			buffer_s = new short[buffer_size];
			buffer_f = new float[buffer_size];

			for (int i=0;i<stat_arr.length;++i) stat_arr[i]=0.0f;

			valid = true;
		} else {
			valid = false;
		}
	}
	@Override protected void finalize() {
		if (!valid) return;
		recorder.release();
	}

	void start() {
		if (!valid) return;
		recorder.startRecording();
	}
	void stop() {
		if (!valid) return;
		recorder.stop();
	}

	void update() {
		if (!valid) return;

		int num_read;
		switch (SAMPLE_ENCODING) {
			case AudioFormat.ENCODING_PCM_FLOAT:
				num_read = recorder.read(buffer_f, offset,buffer_f.length-offset, AudioRecord.READ_NON_BLOCKING);
				break;
			case AudioFormat.ENCODING_PCM_16BIT:
				num_read = recorder.read(buffer_s, offset,buffer_s.length-offset, AudioRecord.READ_NON_BLOCKING);
				for (int i=0;i<buffer_s.length;++i) {
					buffer_f[i] = buffer_s[i]/32768.0f;
				}
				break;
			case AudioFormat.ENCODING_PCM_8BIT:
				num_read = recorder.read(buffer_b, offset,buffer_b.length-offset, AudioRecord.READ_NON_BLOCKING);
				for (int i=0;i<buffer_s.length;++i) {
					buffer_f[i] = (buffer_b[i]-128.0f)/128.0f;
				}
				break;
			default:
				assert false; return;
		}
		if (num_read>0); else return;
		_ts_last_event = System.nanoTime();

		assert SAMPLE_RATE % Config.STAT_RATE == 0;
		int BUF_PER_VOL = SAMPLE_RATE / Config.STAT_RATE;
		int total = offset + num_read;
		int added = total / BUF_PER_VOL; //Number of new max volume sample
		int valid = added * BUF_PER_VOL; //Number of valid raw samples
		int deferred = total % BUF_PER_VOL; //Number of raw samples deferred to next time

		//Shift volume samples
		for (int i=stat_arr.length-added-1;i>=0;--i) {
			stat_arr[i+added] = stat_arr[i];
		}
		//Insert new volume samples
		for (int i=0;i<added;++i) {
			float max = 0.0f;
			int base_sample = i*BUF_PER_VOL;
			for (int j=0;j<BUF_PER_VOL;++j) {
				float sample = buffer_f[base_sample+j];
				max = Math.max(max,Math.abs(sample));
			}
			stat_arr[i] = max;
		}
		//Shift unusable samples forward so we can add onto them and hopefully use them next time.
		switch (SAMPLE_ENCODING) {
			case AudioFormat.ENCODING_PCM_FLOAT: for (int i=0;i<deferred;++i) { buffer_f[i]=buffer_f[valid+i]; } break;
			case AudioFormat.ENCODING_PCM_16BIT: for (int i=0;i<deferred;++i) { buffer_s[i]=buffer_s[valid+i]; } break;
			case AudioFormat.ENCODING_PCM_8BIT:  for (int i=0;i<deferred;++i) { buffer_b[i]=buffer_b[valid+i]; } break;
			default: assert false; return;
		}
		offset = deferred;
	}
}
