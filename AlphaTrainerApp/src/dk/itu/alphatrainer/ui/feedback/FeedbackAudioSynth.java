package dk.itu.alphatrainer.ui.feedback;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;

public class FeedbackAudioSynth implements IFeedbackUi {

	private final static String TAG = "FeedbackAudio";
	private int alphaLevelsReceived = 0;
	private int[] recentHistory;
	private final int recentHistorySize = 2;
	private final static int PITCH_INITIAL = 500;
	private final static int PITCH_FACTOR = PITCH_INITIAL / 150;
	private int currentPitch = PITCH_INITIAL - PITCH_FACTOR * 50;
	private Synthesizer synthesizer;	
	private TextView mView;

	
	
	
	public FeedbackAudioSynth(View view) {
		mView = (TextView) view;
		setupSoundPlayer();
		recentHistory = new int[recentHistorySize];
	}
	

	private void setupSoundPlayer() {
		synthesizer = new Synthesizer();
		synthesizer.initSounds();
	}

	@Override
	public void drawFeedback(int i) {
		
		// update total and recent history
		recentHistory[alphaLevelsReceived++ % recentHistorySize] = i;
		int recentHistoryAvg = Utils.sumOfIntArray(recentHistory) / Math.min(alphaLevelsReceived, recentHistorySize);
		
		Log.i(TAG, "i: " + i + " - recentHistoryAvg: " + recentHistoryAvg);
		
		// animatePitch(currentPitch, 220 + 2 * i);
		animatePitch(currentPitch, PITCH_INITIAL - PITCH_FACTOR * recentHistoryAvg);

		// Update UI 
		UiUtils.simpleTextLoader(mView, i);
		
	}
	
	
	private void animatePitch(final int pitchFrom, final int pitchTo) {
		
		final int difference = pitchTo - pitchFrom;
		final int offset = difference > 0 ? Math.min(pitchFrom, pitchTo) : Math.max(pitchFrom, pitchTo);
		
		new Thread() {
			public void run() {
				/*
				 * The for-loop values are found experimentally
				 * Goal is to make smooth pitch transitions which depend on the (min) buffer size inside Synthesizer
				 */
				for (float x = 0.0f; x < 1.0f; x += 0.005f) {
					SystemClock.sleep(5);
					synthesizer.setPitch(((int) (x * difference)) + offset);
				}
			}
		}.start();
		
		Log.d(TAG, "animatePitch() - pitchFrom: " + pitchFrom + " - pitchTo: " + pitchTo); 
		
		currentPitch = pitchTo;
	}
	
	
	@Override
	public void stop() {
		synthesizer.stop();
	}

	
	/*
	 * This private class handles the generation and playback of a sine wave
	 */
	private class Synthesizer {

		private Thread t;
		private int sr = 44100;
		private boolean isRunning = true;
		private int pitch;

		public void initSounds() {

			// start a new thread in which to synthesise audio
			t = new Thread() {
				public void run() {
					// set process priority
					setPriority(Thread.MAX_PRIORITY);
					// set the buffer size
					int buffsize = AudioTrack.getMinBufferSize(sr,
							AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
					// create an audiotrack object
					AudioTrack audioTrack = new AudioTrack(
							AudioManager.STREAM_MUSIC, sr,
							AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_16BIT, buffsize,
							AudioTrack.MODE_STREAM);

					short samples[] = new short[buffsize];
					int amp = 10000;
					double twopi = 8. * Math.atan(1.);
					double fr = 440.f;
					double ph = 0.0;

					// start audio
					audioTrack.play();

					// synthesis loop
					while (isRunning) {
						fr = pitch; // 440 + 440 * pitch;
						for (int i = 0; i < buffsize; i++) {
							samples[i] = (short) (amp * Math.sin(ph));
							ph += twopi * fr / sr;
						}
						audioTrack.write(samples, 0, buffsize);
					}
					audioTrack.stop();
					audioTrack.release();
				}
			};
			t.start();
		}

		public void setPitch(int i) {
			pitch = i;
		}
		
		public void stop() {
			isRunning = false;
		}
	}

}
