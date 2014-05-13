package dk.itu.alphatrainer.feedback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import dk.itu.alphatrainer.ActivityTraining;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.cloud.PostRecordingToServiceTask;
import dk.itu.alphatrainer.factories.SignalProcessorFactory;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.interfaces.IHeadsetDataListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessingListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessor;
import dk.itu.alphatrainer.model.AlphaMinMax;
import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.util.Utils;

/**
 * This class wires between headset and signal processor and it is input for
 * feedback visualization. 
 */

public class FeedbackLogic implements ISignalProcessingListener,
		IHeadsetDataListener {

	private static final String TAG = FeedbackLogic.class.getName();
	private ISignalProcessor processor;
	private IFeedbackUi feedbackUi;
	private float alphaPeakFq;
	private int sampleRate;
	private float minAlphaLevel;
	private float maxAlphaLevel;
	private BaselineLogic baselineLogic;
	private float[][] dataRecorded;
	private int recordingStart = -1;
	private int recordingEnd;
	private float alphaLevelSum;
	private boolean stopped = false;
	private int secondsLengthSeconds;
	private boolean reverse;

	public FeedbackLogic(IFeedbackUi feedbackUi, int seconds, boolean isBaseline) {
		
		processor = SignalProcessorFactory.getSignalProcessor(this);

		this.secondsLengthSeconds = seconds;
		this.feedbackUi = feedbackUi;
		alphaPeakFq = App.getInstance().getSessionManager().getAlphaPeakFrequency();
		sampleRate = App.getInstance().getHeadsetManager().getHeadset().getSampleRate();
		reverse = App.getInstance().getSessionManager().getReverseFeedback();

		dataRecorded = new float[App.getInstance().getHeadsetManager().getHeadset().getNrOfChannels()][];
		float[] alphaLevelMinMax = App.getInstance().getSessionManager().getAlphaLevelMinMax();
		minAlphaLevel = alphaLevelMinMax[0] > 0.0f ? alphaLevelMinMax[0] : Utils.DEFAULT_ALPHA_LEVEL_MIN;
		maxAlphaLevel = alphaLevelMinMax[1] > 0.0f ? alphaLevelMinMax[1] : Utils.DEFAULT_ALPHA_LEVEL_MAX;
		
		Log.d(TAG, "minAlphaLevel: " + minAlphaLevel + " | maxAlphaLevel: " + maxAlphaLevel);
		
		if (isBaseline) {
			this.baselineLogic = new BaselineLogic();
		}
		
		setupTimer(seconds);
		
		App.getInstance().getHeadsetManager().subscribeData(this);
	}
	
	public void stop() {
		stopped = true;
		feedbackUi.stop();
		App.getInstance().getHeadsetManager().unsubscribeData(this);
	}
	
	
	private void setupTimer(int seconds) {
		new CountDownTimer(seconds * 1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				if (!stopped) {
					onRecordingEnded();
				}
			}
		}.start();
	}
	
	
	private void onRecordingEnded() {
		
//		if audio_clips feedback, notify that training has ended
//		if (App.getInstance().getSessionManager().getFeedbackUiType().equals(App.getInstance().getString(R.string.feedback_audio_clips))) {
//			App.getInstance().playNotificationAndVibrate();
//		}
		
		// when baseline/recording is done, we notify the user
		App.getInstance().playNotificationAndVibrate();
		
		feedbackUi.stop();
		
		if (baselineLogic != null) {
			baselineLogic.saveBaselineToSettings();
		}
		
		App.getInstance().getHeadsetManager().unsubscribeData(this);

		// save recording to file
		new SaveRecordingToFileTask(baselineLogic, dataRecorded).execute();
  		
  		// save recording to database
  		String recordingType = baselineLogic != null ? Recording.TYPE_BASELINE : Recording.TYPE_FEEDBACK;
  		float average = alphaLevelSum / (float) (recordingEnd-recordingStart+1);
  		int recordingId = App.getInstance().getDAO().addRecording(
  				minAlphaLevel, 
  				maxAlphaLevel, 
  				alphaPeakFq, 
  				average,  
  				recordingStart, 
  				recordingEnd, 
  				recordingType,
  				secondsLengthSeconds,
  				App.getInstance().getSessionManager().getFeedbackUiType(),
  				App.getInstance().getSessionManager().getHeadsetType()
  				);
		
  		// save recording to external service
  		new PostRecordingToServiceTask().execute(recordingId);  		
  		
		// finally do
		Intent mIntent = new Intent(App.getInstance(), ActivityTraining.class);
		
		// add information about current activity
		if (baselineLogic != null) {
			mIntent.putExtra(ActivityTraining.BASELINE_COMPLETED, true);
		}
		else {
			mIntent.putExtra(ActivityTraining.TRAINING_COMPLETED, true);
		}
		
		Log.i(TAG, "feedback done, starting activity -> ActivityTraining");
		App.getInstance().getAppContext().startActivity(mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	

	/*
	 * This callback method is called when a headset receives raw data.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see dk.itu.alphatrainer.interfaces.IHeadsetListener#onDataPacket(int,
	 * int[][])
	 */
	@Override
	public void onDataPacket(int channels, float[][] data) {
		Log.d(TAG, "onDataPacket()");

		processor.getBandPower(data, sampleRate, alphaPeakFq);
		
		// NIECETOHAVE: get rid of null check and perhaps do it in Utils.append2Array() ? 
		if (dataRecorded[0]==null) {
			dataRecorded = data;
		} else {
			dataRecorded = Utils.append2Array(dataRecorded, data);
		}
		
		Log.d(TAG, "dataRecorded[0].length: " + dataRecorded[0].length);
		
	}

	@Override
	public void onSignalProcessed(float bandPower) {
		Log.d(TAG, "onSignalProcessed() - bandPower: " + bandPower);
		
		alphaLevelSum += bandPower;
		
		int normalizedAlphaLevel = getNormalizedAlphaLevel(bandPower);
		 
		// lets handle a potential feedbackUi == null graceful 
		try {
		
			if (baselineLogic != null) {
				baselineLogic.onDataReceive(bandPower);
				feedbackUi.drawFeedback(getRandomNormalizedAlphaLevel());
			}
			else {
				feedbackUi.drawFeedback( reverse ? 100 - normalizedAlphaLevel : normalizedAlphaLevel);
			}

		} catch (Exception e) {
			Log.wtf(TAG, "Alert! - There is probably a Feedback UI element that is not set up right", e);
		}

		// save alpha level to database
		int index = App.getInstance().getDAO().addAlphaLevel(bandPower, normalizedAlphaLevel);
		
		// keep track of first and last alpha levels index
		if (recordingStart < 0) recordingStart = index;
		recordingEnd = index;
	}

	private int getNormalizedAlphaLevel(float alphaLevel) {
		
		int normalizedAlphaLevel;
		
		// normalize alpha level to range [0 - 100]
		if (alphaLevel <= minAlphaLevel) {
			normalizedAlphaLevel = 0;
		}
		else if (alphaLevel >= maxAlphaLevel) {
			normalizedAlphaLevel = 100;
		}
		else {
			// mapping alphaLevel from range [minAlphaLevel - maxAlphaLevel] to range [0f - 100f]
			// formula from http://stackoverflow.com/questions/345187/math-mapping-numbers
			// Y = (X-A)/(B-A) * (D-C) + C
			normalizedAlphaLevel = Math.round((alphaLevel - minAlphaLevel) / (maxAlphaLevel - minAlphaLevel) * (100f - 0f) + 0f);
		}
		
		Log.d(TAG, "nomalized: " + normalizedAlphaLevel + " | min: " + minAlphaLevel + " | max: " + maxAlphaLevel + " | input: " + alphaLevel);
		return normalizedAlphaLevel;
	}
	
	private int getRandomNormalizedAlphaLevel() {
		return Math.round((float)Math.random()*100.0f);
	}
	
	
	/*
	 * class for handling logic related to baseline checking and recording
	 */
	private class BaselineLogic {
				
		private List<Float> alphaLevels = new ArrayList<Float>();
		
		public BaselineLogic() {}
		
		public void onDataReceive(float alphaLevel) {
			alphaLevels.add(alphaLevel);
		}
		
		public void saveBaselineToSettings() {
			
			final AlphaMinMax alphaMinMax = processor.getMinMax(Utils.floatListToArray(alphaLevels));
			
			App.getInstance().getSessionManager().setAlphaLevelMinMax(alphaMinMax.getAlphaMin(), alphaMinMax.getAlphaMax());
			App.getInstance().getSessionManager().setBaselineTimestamp(new Date().getTime());
			Log.d(TAG, "Settings saved - alphaMin: " + alphaMinMax.getAlphaMin() + " | alphaMax: " + alphaMinMax.getAlphaMax());
		}

	}
	

	/*
	 * Save recording to file on sd card
	 * - some calculations might take time here 
	 *   - array to string
	 *   - the actual write to sdcard
	 *   
	 * Strictly speaking AsyncTask is related to the UI thread for minor tasks 
	 * because the save recording is related to the the ActivityBaseline we keep
	 * it here - but for another approach it could be an external thread pool / future task handling saving files
	 * like http://pastebin.com/pPcJCEzu - there is a full example here http://www.javacodegeeks.com/2013/07/java-futuretask-example-program.html
	 */
	 private class SaveRecordingToFileTask extends AsyncTask<Void, Void, Void> {

		 	/* all fields has to be either final or volatile here*/  
			private final BaselineLogic baselineLogic;
			private final float[][] dataRecorded;
			
			public SaveRecordingToFileTask(BaselineLogic baselineLogic, float[][] dataRecorded){
				super();
				this.baselineLogic = baselineLogic;
				this.dataRecorded = dataRecorded;
			}
		
		/*
		 * Same data as a file
		 * 
		 * for now we do it as one channel only - multi channels will also require a more sane file approach like a *edf* file 
		 * NIECETOHAVE ^^
		 * 
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
				  
	 		String fileName = baselineLogic != null ? "baseline.txt" : "feedback.txt"; 
	   		byte[] rawBytes =	Arrays.toString(dataRecorded[0]).getBytes();
	   		
	   		try { 
	   			boolean success = App.getInstance().getDAO().saveByteArrayToSDCard(fileName, rawBytes, true); 
	   			Log.d(TAG, fileName + " saved successfully to sd card: " + success); 
	   		}
	   		catch (IOException e) {}
			return null;
		}


	 }
	
}

