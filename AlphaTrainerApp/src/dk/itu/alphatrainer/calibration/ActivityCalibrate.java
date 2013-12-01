package dk.itu.alphatrainer.calibration;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.FragmentActivityLifecycleManagementFullscreen;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.factories.SignalProcessorFactory;
import dk.itu.alphatrainer.interfaces.IHeadsetDataListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessingListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessor;
import dk.itu.alphatrainer.ui.UiUtils;

public class ActivityCalibrate extends FragmentActivityLifecycleManagementFullscreen implements ISignalProcessingListener {

	private static final String TAG = ActivityCalibrate.class.getName();
	private ISignalProcessor processor;
	private static final String KEY_ALPHA_PEAK = "alpha_peak";
	private CalibrationDataHandler calibrationDataHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		processor = SignalProcessorFactory.getSignalProcessor(this);
		
		float alphaPeak = getIntent().getExtras() != null ? getIntent().getExtras().getFloat(KEY_ALPHA_PEAK) : -1f;
		Log.i(TAG, "alphaPeak: " + alphaPeak);
		
		if (alphaPeak > 0) {
			
			// resemble full screen theme
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.activity_calibrate_done);
			super.onCreate(savedInstanceState);
			UiUtils.changeFonts((ViewGroup) findViewById(android.R.id.content));

			TextView txtAlphaPeak = (TextView) findViewById(R.id.txt_alpha_peak);
			// we must explicitly parse our float to string before setting it as text of a textview - it compiles fine but gives runtime error
			txtAlphaPeak.setText(Float.toString(alphaPeak));
			
			// setup continue button - appears when calibration is done
			Button btnContinue = (Button) findViewById(R.id.btn_continue);
			btnContinue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish(); // resembles a push on the back button
				}
			});
			
		}
		else {
			
			setContentView(R.layout.activity_calibrate);
			super.onCreate(savedInstanceState);
			UiUtils.changeFonts((ViewGroup) findViewById(android.R.id.content));
			
			// setup stop button - appears when touching the screen, otherwise it is hidden
			Button btnStop = (Button) findViewById(R.id.btn_stop);
			btnStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish(); // resembles a push on the back button
				}
			});
			
			makeCalibration();
			
		}
		
	}


	public void onCalibrationRecordingDone(float[][] data, int Fs) {
		
		App.getInstance().playNotificationAndVibrate();

		Log.d(TAG, "onCalibrationRecordingDone() called");

		if (data.length > 0 && data[0].length > 0) {
			float alphaPeak = processor.getAlphaPeak(data, Fs);
			Log.d(TAG, "opencv based getAlphaPeak(): " + alphaPeak);
			App.getInstance().getSessionManager().setAlphaPeakFrequency(alphaPeak);
			App.getInstance().getSessionManager().setCalibrationTimestamp(App.getInstance().getDate().getTime());
			// start another instance of this activity but with alpha peak (will not be a fullscreen activity)
			Intent intent = new Intent(this, ActivityCalibrate.class);
			intent.putExtra(KEY_ALPHA_PEAK, alphaPeak);
			startActivity(intent);
		}
		else {
			Log.e(TAG, "alphaPeak not calculated - dataRecorded was empty");
		}
		
	}

	
	/*
	 * Do the calibration
	 */
	public void makeCalibration() {
		calibrationDataHandler = new CalibrationDataHandler(this);
	}
	
	
	@Override
	protected void onPause() {
		// when application is sent to background, stop calibrating and finish this activity
		if (calibrationDataHandler != null) {
			calibrationDataHandler.stop();
		}
		finish();
		super.onPause();
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
        
		Log.d(TAG, "onWindowFocusChanged() - hasFocus: " + hasFocus);
		
		super.onWindowFocusChanged(hasFocus);
        
        // make sure screen has maximum brightness when in focus
        if (hasFocus) {
            final WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1F;
            layout.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            getWindow().setAttributes(layout);
        }
    }
	

	/**
	 * 
	 * Inner class or simply let ActivityCalibrate implement IHeadsetListener?
	 * 
	 * Pro: let it be here then its possible to move out into a separate class
	 * easily and why don't we do it right away because no body else out side
	 * current class are using it.
	 * 
	 */
	private class CalibrationDataHandler implements IHeadsetDataListener {

		private static final String TAG = "CalibrationDataHandler";
		private float[][] dataRecorded;
		private int dataRecordedInsertIndex = 0;
		private int Fs;
		private int recordingLength;
		private int numberOfReceivedSamples;
		private int numberOfSamplesDiscartedStart;
		private int numberOfSamplesDiscartedEnd;
		private ActivityCalibrate parent;
		private TextView mTextView;

		public CalibrationDataHandler(ActivityCalibrate parent) {
			this.parent = parent;
			App.getInstance().getHeadsetManager().subscribeData(this);
			Fs = App.getInstance().getHeadsetManager().getHeadset().getSampleRate();
			int numberOfChannels = App.getInstance().getHeadsetManager().getHeadset().getNrOfChannels();
			recordingLength = 30 * Fs;
			numberOfReceivedSamples = 0;
			numberOfSamplesDiscartedStart = 10 * Fs;
			numberOfSamplesDiscartedEnd = 5 * Fs;
			dataRecorded = new float[numberOfChannels][recordingLength];
			mTextView = (TextView) parent.findViewById(R.id.txt_calibration);

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
			Log.d(TAG, "onDataPacket: " + dataRecordedInsertIndex
					+ " recordingLength: " + recordingLength);

			boolean storeData = numberOfReceivedSamples >= numberOfSamplesDiscartedStart
					&& dataRecordedInsertIndex < recordingLength;
			if (storeData) {

				// int index = numberOfReceivedSamples -
				// numberOfSamplesDiscartedStart;

				for (int i = 0; i < data.length; i++) { // loops through
														// channels

					Log.d(TAG, "onDataPacket - numberOfReceivedSamples: "
							+ numberOfReceivedSamples);

					int copyLength = data[i].length;
					if (dataRecordedInsertIndex + data[0].length > recordingLength)
						copyLength = recordingLength - dataRecordedInsertIndex;

					System.arraycopy(data[i], 0, dataRecorded[i],
							dataRecordedInsertIndex, copyLength);

					dataRecordedInsertIndex += data[i].length;

				}
			}

			int finalLengthSamples = recordingLength + numberOfSamplesDiscartedStart + numberOfSamplesDiscartedEnd;
			Log.d(TAG, "finalLengthSamples = recordingLength + numberOfSamplesDiscartedStart + numberOfSamplesDiscartedEnd: "
					+ finalLengthSamples);
			Log.d(TAG, "numberOfReceivedSamples: " + numberOfReceivedSamples);
			if (numberOfReceivedSamples >= finalLengthSamples) {
				
				/* NIECETOHAVE: change into a sane throw exception
				 * assert wont work on an Android device
				 * http://stackoverflow.com/questions/6176441/how-to-use-assert-in-android
				 * */
				assert(dataRecordedInsertIndex == recordingLength);
				
				unsubscribe();
				
				parent.onCalibrationRecordingDone(dataRecorded, Fs);
			}

			numberOfReceivedSamples += data[0].length;
			

			// Update UI 
			UiUtils.simpleTextLoader(mTextView, 0);


		}
		
		public void stop() {
			unsubscribe();
		}
		
		private void unsubscribe() {
			App.getInstance().getHeadsetManager().unsubscribeData(this);
		}

	}


	@Override
	public void onSignalProcessed(float bandPower) {
		// Do nothing
	}

}
