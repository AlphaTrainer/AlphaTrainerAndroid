package dk.itu.alphatrainer.ui.feedback.xtra;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.SystemClock;
import android.util.Log;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;

public class FeedbackAudioSample implements IFeedbackUi {

	private final static String TAG = "FeedbackAudio";
	private static final int TRANSITION_STEPS = 100;
	private static final int TRANSITION_DURATION = 1000;
	private int currentPitch = 440;
	private MediaPlayerWrapper mediaPlayerWrapper;
	private SoundPoolWrapper soundPoolWrapper;
	private Synthesizer synthesizer;

	private static final int MEDIAPLAYER = 1;
	private static final int SOUNDPOOL = 2;
	private static final int SYNTHESIZER = 3;
	private static final int soundPlayer = MEDIAPLAYER;

	
	public FeedbackAudioSample() {
		setupSoundPlayer();
	}
	

	private void setupSoundPlayer() {

		switch (soundPlayer) {

		case MEDIAPLAYER:
			mediaPlayerWrapper = new MediaPlayerWrapper();
			mediaPlayerWrapper.initSounds(App.getInstance());
			break;

		case SOUNDPOOL:
			soundPoolWrapper = new SoundPoolWrapper();
			soundPoolWrapper.initSounds(App.getInstance());
			break;
			
		case SYNTHESIZER:
			synthesizer = new Synthesizer();
			synthesizer.initSounds();
			break;
		}

	}

	@Override
	public void drawFeedback(int i) {
		// int heightTo = i * screenHeight / 100;
		// animateHeight(currentHeight, heightTo);

		switch (soundPlayer) {

		case MEDIAPLAYER:
			mediaPlayerWrapper.setVol(i);
			break;

		case SOUNDPOOL:
			// soundPoolWrapper
			break;
			
		case SYNTHESIZER:
			// synthesizer.setPitch(i);
			
			animatePitch(currentPitch, 440 + 4 * i);
			break;
		}

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
	}

	
	
	/*
	 * This private class encapsulates logic of playing back audio using the MediaPlayer
	 * 
	 * With MediaPlayer, we could go for an audio feedback made with
	 * reverb since this is already in the android audio api:
	 * http://developer
	 * .android.com/reference/android/media/audiofx/AudioEffect.html
	 * http://developer
	 * .android.com/reference/android/media/MediaPlayer.html#
	 * attachAuxEffect%28int%29
	 * http://stackoverflow.com/questions/10409122/android
	 * -mediaplayer-with-audioeffect-getting-error-22-0
	 */
	private class MediaPlayerWrapper {

		public static final int FEEDBACK_AMBIENT = R.raw.feedback_ambient;

		MediaPlayer mp;
		final int sampleRate = AudioTrack
				.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);

		public void initSounds(Context context) {

			mp = MediaPlayer.create(App.getInstance(), FEEDBACK_AMBIENT);
			mp.setLooping(true);
			mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					Log.i(TAG, "onPrepared() called");
					mp.start();

				}
			});
		}

		public void setVol(int alphaLevel) {
			float vol = (float) alphaLevel / 100.0f;
			Log.i(TAG, "new vol: " + vol);
			mp.setVolume(vol, vol);
		}

	}

	private class SoundPoolWrapper {

		/*
		 * This private class encapsulates logic of playing back audio using the
		 * SoundPool
		 * 
		 * SoundPool is a thinner wrapper around AudioTrack which is the type
		 * used for playing audio in case of both MediaPlayer and SoundPool - it
		 * has ability to set pitch - it is generally adviced to be used for
		 * short clips
		 * 
		 * Had some problems with a long load time for a 40 seconds clip in both
		 * mp3 and waw when trying to set it to loop - "Error loading track"
		 * Without loop set to true, we can play about 3 seconds of the 40
		 * second clip
		 * 
		 * Can load and play a shorter clip of 5 seconds, but it still won't
		 * loop - gives "Error loading track"
		 * 
		 * With SoundPool, we could design an audio feedback by playing
		 * short sounds and let which sound and how many sounds represent the
		 * alpha feedback (e.g. fewer and more "pleasent" sounds represents high
		 * alpha).
		 */

		public static final int FEEDBACK_AMBIENT = R.raw.feedback_ambient;
		public static final int FEEDBACK_GROOVE = R.raw.feedback_groove;

		private SoundPool soundPool;
		private HashMap<Integer, Integer> soundPoolMap;

		public void initSounds(Context context) {

			soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
			soundPoolMap = new HashMap<Integer, Integer>();

			soundPoolMap.put(FEEDBACK_GROOVE,
					soundPool.load(context, FEEDBACK_GROOVE, 1));

			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {

					Log.d(TAG, "playSound() called");

					playSound(App.getInstance(), FEEDBACK_GROOVE);

				}
			});

		}

		public void playSound(Context context, int soundID) {

			// example playback
			// soundPlayer.playSound(getApplicationContext(),
			// SoundPlayer.FEEDBACK_AMBIENT);

			Log.d(TAG, "inside playSound()");

			float volume = 1.0f; // whatever in the range = 0.0 to 1.0

			// play sound with same right and left volume, with a priority of 1,
			// zero repeats (i.e play once), and a playback rate of 1f
			// one
			soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
		}

		public void setRate(int soundID, float rate) {
			soundPool.setRate(soundID, rate);
		}

	}

	private class Synthesizer {

		private Thread t;
		private int sr = 44100;
		private boolean isRunning = true;
		private int pitch;

		public void initSounds() {

			// start a new thread to synthesise audio
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
	}

}
