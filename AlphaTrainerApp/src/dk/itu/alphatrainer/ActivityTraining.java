package dk.itu.alphatrainer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import dk.itu.alphatrainer.baseline.ActivityBaseline;
import dk.itu.alphatrainer.calibration.ActivityCalibrate;
import dk.itu.alphatrainer.feedback.ActivityFeedback;
import dk.itu.alphatrainer.interfaces.IHeadsetConnectionStatusListener;
import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;

public class ActivityTraining extends FragmentActivityLifecycleManagementFullscreen implements IHeadsetConnectionStatusListener {

	public final static String BASELINE_COMPLETED = "baseline_completed";
	public final static String TRAINING_COMPLETED = "training_completed";
	private final static String TAG = ActivityTraining.class.getName();
	private final static int MAX_HOURS_BETWEEN_CALIBRATION = 8;
	
	private Button btnCalibrate;
	private Button btnBaseline;
	private Button btnFeedback;
	private TextView txtDescription;
	private Button btnStartNextActivity;
	
	private boolean manualMode;
	private boolean initialDescriptionShown;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		UiUtils.changeFonts((ViewGroup) findViewById(android.R.id.content));
		
		setMode();
		
		/*
		 * Get reference to views
		 */
		btnCalibrate = (Button) findViewById(R.id.btn_calibrate);
		btnBaseline = (Button) findViewById(R.id.btn_baseline);
		btnFeedback = (Button) findViewById(R.id.btn_feedback);
		txtDescription = (TextView) findViewById(R.id.txt_description);
		btnStartNextActivity = (Button) findViewById(R.id.btn_start_next_activity);
		
		/*
		 * Setup connection status support
		 * - used to enable disable buttons etc
		 */
		App.getInstance().getHeadsetManager().subscribeConnectionStatus(this);
		
		
		/*
		 * Setup button click handlers
		 * 
		 * NIECETOHAVE: Also implement enable/diable buttons based on connectivity
		 * 
		 */
		
		if (manualMode) {
		
			btnCalibrate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startCalibration();
				}
			});
			btnCalibrate.setVisibility(View.VISIBLE);
			
			btnBaseline.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startBaseline();
				}
			});
			btnBaseline.setVisibility(View.VISIBLE);
			
			btnFeedback.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startFeedback();
				}
			});
			btnFeedback.setVisibility(View.VISIBLE);
			
		}
	}
	
	@Override
	protected void onResume() {
		
		Log.d(TAG, "onResume()");
		
		super.onResume();
		
		setMode();
		if (!manualMode) {
			handleTrainingFlow();
		}
	}
	
	
	/*
	 * This method has the responsibility for the app flow by taking appropriate actions according to app state.
	 * By state is meant:
	 * - how long since last calibration
	 * - how long since last baseline
	 * 
	 * NIECETOHAVE: give this if/else some love most of the time its the same setOnClickListener() in the air 
	 */
	private void handleTrainingFlow() {
		
		Log.d(TAG, "handleTrainingFlow()");
		
		// if user has not calibrated for MAX_HOURS_BETWEEN_CALIBRATION hours
		if (hasNotBeenPerformedRecently(App.getInstance().getSessionManager().getCalibrationTimestamp(), MAX_HOURS_BETWEEN_CALIBRATION)) {
			Log.d(TAG, "flow: calibration");

			// set appropriate description and button text
			txtDescription.setText(getString(R.string.calibration_description));
			
			btnStartNextActivity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startCalibration();
				}
			});
		}
		// if user has not recorded a baseline *today*
		else if (hasNotBeenPerformedRecently(App.getInstance().getSessionManager().getBaselineTimestamp())) {
			Log.d(TAG, "flow: baseline");
			
			// set appropriate description and button text
			txtDescription.setText(getBaselineDescription());
			
			btnStartNextActivity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startBaseline();
				}
			});
				
		}
		// if user is ready to actual training
		else {
			Log.d(TAG, "flow: training");

			// set appropriate feedback description
			txtDescription.setText(getFeedbackDescription());
			
			btnStartNextActivity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startFeedback();
				}
			});
			
			// NIECETOHAVE: !initialDescriptionShown might not be needed - take a look with fresh a fresh brain
			if (!initialDescriptionShown && getIntent().getExtras() != null) {

				// we can also check for BASELINE_COMPLETED
				if (getIntent().getExtras().containsKey(TRAINING_COMPLETED)) {
					
					int performance = App.getInstance().getDAO().getMostRecentPerformance(Recording.TYPE_FEEDBACK);
					Log.i(TAG, "performance after training: " + performance);
					
					txtDescription.setText(getString(R.string.feedback_description_completed).concat("\n\nPerformance: " + performance + "%"));
					
					btnStartNextActivity.setText(getString(R.string.btn_try_again));
				}
				
				btnStartNextActivity.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// not used for now:
						// initialDescriptionShown = true; handleTrainingFlow();
						startFeedback();
					}
				});
			}
			
		}
		
		txtDescription.setVisibility(View.VISIBLE);
		btnStartNextActivity.setVisibility(View.VISIBLE);
	}
	
	private void startCalibration() {
		startActivity(new Intent(this, ActivityCalibrate.class));
	}
	
	private void startBaseline() {
		startActivity(new Intent(this, ActivityBaseline.class));
	}
	
	private void startFeedback() {
		
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey(TRAINING_COMPLETED)) {
				getIntent().removeExtra(TRAINING_COMPLETED);
			}
		}
		
		startActivity(new Intent(this, ActivityFeedback.class));
	}

	
	private String getFeedbackDescription() {
		
		String feedbackUiType = App.getInstance().getSessionManager().getFeedbackUiType();
		
		Log.d(TAG, "getFeedbackDescription() - feedbackUiType: " + feedbackUiType);
		
		if (feedbackUiType.equals(getString(R.string.feedback_audio_clips))) {
			return getString(R.string.feedback_audio_clips_description);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_audio_synth))) {
			return getString(R.string.feedback_audio_synth_description);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_vibrate))) {
			return getString(R.string.feedback_vibration_description);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_collision))) {
			return getString(R.string.feedback_colision_description);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_box))) {
			return getString(R.string.feedback_box_description);
		}
		else 
			return "NO FEEDBACK DESCRIPTION FOUND";
	}
	
	private String getBaselineDescription() {
	
		String feedbackUiType = App.getInstance().getSessionManager().getFeedbackUiType();
		
		Log.d(TAG, "getBaselineDescription() - feedbackUiType: " + feedbackUiType);
		
		if (feedbackUiType.equals(getString(R.string.feedback_audio_clips)) || 
				feedbackUiType.equals(getString(R.string.feedback_audio_synth))) {
			return getString(R.string.baseline_description_audio);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_vibrate))) {
			return getString(R.string.baseline_description_vibration);
		}
		else if (feedbackUiType.equals(getString(R.string.feedback_collision)) ||
				feedbackUiType.equals(getString(R.string.feedback_box))) {
			return getString(R.string.baseline_description_visual);
		}
		else 
			return "NO BASELINE DESCRIPTION FOUND";
	}
	
	private void setMode() {
		manualMode = App.getInstance().getSessionManager().getAppMode().equals(getString(R.string.mode_manual));
		initialDescriptionShown = false;
	}
	
	/*
	 * Recent based on hour specified 
	 */
	private boolean hasNotBeenPerformedRecently(long activityLastPerformed, int hours) {
		long delta = App.getInstance().getDate().getTime() - activityLastPerformed;
		Log.d(TAG, "delta: " + delta + " - resp: " + (delta - Utils.HOUR_MILLIS * hours >= 0));
		Log.d(TAG, "delta - HOUR_MILLIS * hours (returns true if this is positive): " + (delta - Utils.HOUR_MILLIS * hours));
		return delta - Utils.HOUR_MILLIS * hours >= 0;
	}
	
	/*
	 * Recent in the meaning of *today* 
	 */
	private boolean hasNotBeenPerformedRecently(long activityLastPerformed) {
		long delta = App.getInstance().getDate().getTime() - activityLastPerformed;
		Log.d(TAG, "delta - Utils.getMilisecondPassedSinceMidnight() >= 0: " + (delta - Utils.getMilisecondPassedSinceMidnight() >= 0));
		return delta - Utils.getMilisecondPassedSinceMidnight() >= 0;
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// after this, getIntent() should always return the last received intent
		
		Log.d(TAG, "onNewIntent()");
		
		super.onNewIntent(intent);
	    setIntent(intent);
	}

	@Override
	public void onConnectionStatusUpdate(int connectionStatus) {

		Log.d(TAG, "onConnectionStatusUpdate(int connectionStatus): "+connectionStatus);
		
		String msg;
		if (connectionStatus == HeadsetManager.CONNECTION_STATUS_NOT_FOUND 
			|| connectionStatus == HeadsetManager.CONNECTION_STATUS_NOT_PAIRED 
			|| connectionStatus == HeadsetManager.CONNECTION_STATUS_DISCONNECTED) {
			
			switch (connectionStatus) {			
				case HeadsetManager.CONNECTION_STATUS_NOT_FOUND:
					 msg = getString(R.string.headset_error_headset_not_found);
					 break;
				case HeadsetManager.CONNECTION_STATUS_NOT_PAIRED:
					 msg = getString(R.string.headset_error_headset_not_paired);
					 break;
				case HeadsetManager.CONNECTION_STATUS_DISCONNECTED:
					 msg = getString(R.string.headset_error_headset_not_connected);
					 break;
				default: msg = getString(R.string.headset_error_headset_unknown_error);
					 break;
			}

			txtDescription.setText(msg);
			btnStartNextActivity.setText(getString(R.string.btn_wait));
			btnStartNextActivity.setEnabled(false);
		} else if (connectionStatus >= 100) {
			btnStartNextActivity.setText(getString(R.string.btn_start));
			btnStartNextActivity.setEnabled(true);
		} else {
			btnStartNextActivity.setText(getString(R.string.btn_wait));
			btnStartNextActivity.setEnabled(false);
		}
		
	}
}
