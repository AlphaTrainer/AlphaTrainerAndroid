package dk.itu.alphatrainer.ui.feedback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;

public class FeedbackAudioClips implements IFeedbackUi {

	private final static String TAG = "FeedbackAudio";
	private static final int UPDATE_FREQUENCY = 3; // seconds
	private SoundPoolWrapper soundPoolWrapper;
	@SuppressWarnings("unused") // but required
	private int alphaLevelSum = 0;
	private int alphaLevelsReceived = 0;
	private int[] recentHistory;
	private final int recentHistorySize = 3;
	private long lastSoundUpdate = 0;
	private Timer timer = new Timer();
	private TextView mView;

	
	public FeedbackAudioClips(View view) {
		setupSoundPlayer();
		recentHistory = new int[recentHistorySize];
		mView = (TextView) view;
	}
	

	private void setupSoundPlayer() {

		soundPoolWrapper = new SoundPoolWrapper();
		soundPoolWrapper.initSounds(App.getInstance());

	}

	@Override
	public void drawFeedback(int i) {
		
		// update total and recent history
		recentHistory[alphaLevelsReceived++ % recentHistorySize] = i;
		alphaLevelSum += i;
	
		makeAudioFeedback();
	
		Log.d(TAG, "drawFeedback() - i: " + i);
		
		// Update UI 
		UiUtils.simpleTextLoader(mView, i);
		
	}
	
	
	private void makeAudioFeedback() {
		
		long now = System.nanoTime();
		
		Log.d(TAG, "now - lastSoundUpdate : " + (now - lastSoundUpdate));
		
		// only update every UPDATE_FREQUENCY seconds
		if (now - lastSoundUpdate > UPDATE_FREQUENCY * 1000000000l) {
		
			Log.d(TAG, "updating audio feedback");
			
			int recentAvg = Utils.sumOfIntArray(recentHistory) / Math.min(alphaLevelsReceived, recentHistorySize);
			
			// we play a sound to be played per 30 in (100 - average)
			
			int nrOfSoundsToBePlayed = (100 - recentAvg) / 30;
			
			Log.i(TAG, "recentAvg: " + recentAvg + " - nrOfSoundsToBePlayed: " + nrOfSoundsToBePlayed);
			
			for (int i=0; i<nrOfSoundsToBePlayed; i++) {
			
				timer.schedule(new TimerTask() {
		
				    @Override
				    public void run() {
				    	soundPoolWrapper.playRandomSound();
				    }
				    
				},
				// time before to start calling the TimerTask (in milliseconds)
				Math.round(Math.random() * (UPDATE_FREQUENCY * 1000)));
			}
			
			lastSoundUpdate = now;
			
		}
		else {
			Log.d(TAG, "NOT updating audio feedback");
		}
		
	}

	
	@Override
	public void stop() {
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

		public static final int BELL = R.raw.bell;
		public static final int BELL_1 = R.raw.bell_1;
		public static final int BELL_2 = R.raw.bell_2;
		public static final int BELL_3 = R.raw.bell_3;
		public static final int BELL_4 = R.raw.bell_4;
		public static final int BELL_5 = R.raw.bell_5;
		public static final int BELL_6 = R.raw.bell_6;
		public static final int BELL_7 = R.raw.bell_7;
		public static final int BELL_8 = R.raw.bell_8;
		public static final int BELL_9 = R.raw.bell_9;
		public static final int BELL_10 = R.raw.bell_10;
		public static final int BELL_11 = R.raw.bell_11;
		public static final int BELL_12 = R.raw.bell_12;
		
		public static final int DROPLET_1 = R.raw.droplet1;
		public static final int DROPLET_2 = R.raw.droplet2;
		public static final int DROPLET_3 = R.raw.droplet3;
		public static final int DROPLET_4 = R.raw.droplet4;
		public static final int DROPLET_5 = R.raw.droplet5;
		
		private static final int AUDIO_STREAMS = 12; // max nr 0f simultaniously played sounds
		private static final int BELLS = 1;
		private static final int DROPS = 2;
		private static final int sounds = BELLS;

		private Context context;
		private SoundPool soundPool;
		private HashMap<Integer, Integer> soundPoolMap;
		Random random;
		LimitedQueue<Integer> queue;
		
		// NIECETOHAVE: change into a sparse array
		@SuppressLint("UseSparseArrays")
		public void initSounds(Context c) {
			this.context = c;
			soundPool = new SoundPool(AUDIO_STREAMS, AudioManager.STREAM_MUSIC, 100);
			soundPoolMap = new HashMap<Integer, Integer>();
			random = new Random(System.currentTimeMillis());

			
			// Populate sound pool map
			
			switch (sounds) {
			
			case (BELLS):
				soundPoolMap.put(BELL, soundPool.load(context, BELL, 1));
				soundPoolMap.put(BELL_1, soundPool.load(context, BELL_1, 1));
				soundPoolMap.put(BELL_2, soundPool.load(context, BELL_2, 1));
				soundPoolMap.put(BELL_3, soundPool.load(context, BELL_3, 1));
				soundPoolMap.put(BELL_4, soundPool.load(context, BELL_4, 1));
				soundPoolMap.put(BELL_5, soundPool.load(context, BELL_5, 1));
				soundPoolMap.put(BELL_6, soundPool.load(context, BELL_6, 1));
				soundPoolMap.put(BELL_7, soundPool.load(context, BELL_7, 1));
				soundPoolMap.put(BELL_8, soundPool.load(context, BELL_8, 1));
				soundPoolMap.put(BELL_9, soundPool.load(context, BELL_9, 1));
				soundPoolMap.put(BELL_10, soundPool.load(context, BELL_10, 1));
				soundPoolMap.put(BELL_11, soundPool.load(context, BELL_11, 1));
				soundPoolMap.put(BELL_12, soundPool.load(context, BELL_12, 1));
				break;
				
			case (DROPS):
				soundPoolMap.put(DROPLET_1, soundPool.load(context, DROPLET_1, 1));
				soundPoolMap.put(DROPLET_2, soundPool.load(context, DROPLET_2, 1));
				soundPoolMap.put(DROPLET_3, soundPool.load(context, DROPLET_3, 1));
				soundPoolMap.put(DROPLET_4, soundPool.load(context, DROPLET_4, 1));
				soundPoolMap.put(DROPLET_5, soundPool.load(context, DROPLET_5, 1));
				break;
			}
			
			// we initialize out queue to have at least 1 element and max the number of samples - 3
			int queueSize = soundPoolMap.size() > 1 ? Math.max(1, soundPoolMap.size() - 3) : 0;
			queue = new LimitedQueue<Integer>(queueSize);
			
			
			/*
			 * maybe not relevant since we no longer just load one file for looping playback
			 * 
			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {

					Log.d(TAG, "playSound() called");

					playSound(App.getInstance(), FEEDBACK_GROOVE);

				}
			});
			*/
		}
		
		
		public void playSound(int soundID) {

			Log.d(TAG, "inside playSound()");

			float volume = 1.0f; // whatever in the range = 0.0 to 1.0

			// play sound with same right and left volume, with a priority of 1,
			// zero repeats (i.e play once), and a playback rate of 1f one
			soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
		}
		
		
		public void playRandomSound() {
			Set<Integer> keySetCopy = new TreeSet<Integer>(soundPoolMap.keySet());
			Log.d(TAG, "keySet size init: " + keySetCopy.size());
			
			keySetCopy.removeAll(queue);
			Log.d(TAG, "keySet size after removal of queue elements: " + keySetCopy.size());
			
			Object[] setArray = (Object[]) keySetCopy.toArray();
			int soundToPlay = random.nextInt(setArray.length);
			Log.d(TAG, "soundToPlay: " + soundToPlay);
			playSound((Integer) setArray[soundToPlay]);
			queue.add((Integer) setArray[soundToPlay]);
			Log.d(TAG, "queue size: " + queue.size());
		}
		
		
		/*
		 * not relevant since we now use different samples with different pitch
		 *
		public void setRate(int soundID, float rate) {
			soundPool.setRate(soundID, rate);
		}
		*/
		
	}
	
	@SuppressWarnings("serial")
	private class LimitedQueue<E> extends LinkedList<E> {

	    private final int limit;

	    public LimitedQueue(int limit) {
	        this.limit = limit;
	    }

	    @Override
	    public boolean add(E o) {
	        super.add(o);
	        while (size() > limit) { super.remove(); }
	        return true;
	    }
	}


}
