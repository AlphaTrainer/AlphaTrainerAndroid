package dk.itu.alphatrainer.factories;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.feedback.FeedbackAudioClips;
import dk.itu.alphatrainer.ui.feedback.FeedbackAudioSynth;
import dk.itu.alphatrainer.ui.feedback.FeedbackBox;
import dk.itu.alphatrainer.ui.feedback.FeedbackCollision;
import dk.itu.alphatrainer.ui.feedback.FeedbackVibrate;
import dk.itu.alphatrainer.ui.feedback.FragmentFeedbackAudioClips;
import dk.itu.alphatrainer.ui.feedback.FragmentFeedbackAudioSynth;
import dk.itu.alphatrainer.ui.feedback.FragmentFeedbackBox;
import dk.itu.alphatrainer.ui.feedback.FragmentFeedbackCollision;
import dk.itu.alphatrainer.ui.feedback.FragmentFeedbackVibrate;
import dk.itu.alphatrainer.ui.feedback.xtra.FeedbackBar;
import dk.itu.alphatrainer.ui.feedback.xtra.FeedbackBarGrowing;
import dk.itu.alphatrainer.ui.feedback.xtra.FeedbackBarHistory;
import dk.itu.alphatrainer.ui.feedback.xtra.FeedbackBoxHistory;
import dk.itu.alphatrainer.ui.feedback.xtra.FragmentFeedbackBar;
import dk.itu.alphatrainer.ui.feedback.xtra.FragmentFeedbackBarGrowing;
import dk.itu.alphatrainer.ui.feedback.xtra.FragmentFeedbackBarHistory;
import dk.itu.alphatrainer.ui.feedback.xtra.FragmentFeedbackBoxHistory;

/**
 * 
 * Handles instantiation of feedback ui.
 * 
 */
public class FeedbackUiFactory {

	private final static String TAG = FeedbackUiFactory.class.getName();
	
	/*
	 * Should not be possible to instantiate this class
	 */
	private FeedbackUiFactory() {
	}

	/*
	 * Read which fragment to create from setting. 
	 */
	public static Fragment getFragment() {
		
		String feedbackUiFromSettings = App.getInstance().getSessionManager().getFeedbackUiType();
		
		Log.d(TAG, "FeedbackUiFactory - read from settings: " + feedbackUiFromSettings);
		
		if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_bar)))
			return new FragmentFeedbackBar();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_bar_history)))
			return new FragmentFeedbackBarHistory();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_bar_growing)))
			return new FragmentFeedbackBarGrowing();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_box)))
			return new FragmentFeedbackBox();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_box_history)))
			return new FragmentFeedbackBoxHistory();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_vibrate)))
			return new FragmentFeedbackVibrate();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_collision)))
			return new FragmentFeedbackCollision();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_audio_synth)))
			return new FragmentFeedbackAudioSynth();
		else if (feedbackUiFromSettings.equals(App.getInstance().getString(R.string.feedback_audio_clips)))
			return new FragmentFeedbackAudioClips();		
		
		// a default return statement keeps compiler happy
		return new FragmentFeedbackBar();
	}
	
	
	
	/*
	 * From the view given as input, it is determined which IFeedbackUi implementation should be created and returned
	 */
	public static IFeedbackUi getFeedbackUi(View fragmentRootView) {

		int fragmentRootViewId = fragmentRootView.getId();
		
		Log.d(TAG, "getFeedbackUi() called - fragmentRootView.getId()= " + fragmentRootViewId);
		
		if (fragmentRootViewId == R.id.layout_feedback_box_root) {
			Log.d(TAG, "CREATED BOX");
			return new FeedbackBox(fragmentRootView.findViewById(R.id.view_feedback_box));
		}
		else if (fragmentRootViewId == R.id.layout_feedback_bar_root) {
			Log.d(TAG, "CREATED BAR");
			return new FeedbackBar(fragmentRootView.findViewById(R.id.view_feedback_bar));
		}
		else if (fragmentRootViewId == R.id.layout_feedback_bar_history_root) {
			Log.d(TAG, "CREATED BAR HISTORY");
			return new FeedbackBarHistory(
					fragmentRootView.findViewById(R.id.view_feedback_bar),
					fragmentRootView.findViewById(R.id.view_average_line),
					fragmentRootView);
		}
		else if (fragmentRootViewId == R.id.layout_feedback_box_history_root) {
			Log.d(TAG, "CREATED BOX HISTORY");
			return new FeedbackBoxHistory(
					fragmentRootView.findViewById(R.id.view_feedback_box),
					fragmentRootView);
		}
		else if (fragmentRootViewId == R.id.layout_feedback_bar_growing_root) {
			Log.d(TAG, "CREATED BAR GROWING");
			return new FeedbackBarGrowing(fragmentRootView.findViewById(R.id.view_feedback_bar));
		}
		else if (fragmentRootViewId == R.id.layout_feedback_vibrate_root) {
			Log.d(TAG, "CREATED VIBRATE");
			return new FeedbackVibrate(fragmentRootView.findViewById(R.id.view_feedback_vibrate));
		}
		else if (fragmentRootViewId == R.id.layout_feedback_collision_root) {
			Log.d(TAG, "CREATED COLLISION");
			return new FeedbackCollision(fragmentRootView.findViewById(R.id.view_feedback_collision), 
					(RelativeLayout) fragmentRootView);
		}
		else if (fragmentRootViewId == R.id.layout_feedback_audio_synth_root) {
			Log.d(TAG, "CREATED AUDIO SYNTH");
			return new FeedbackAudioSynth(fragmentRootView.findViewById(R.id.view_feedback_audio_synth));
		}
		else if (fragmentRootViewId == R.id.layout_feedback_audio_clips_root) {
			Log.d(TAG, "CREATED AUDIO CLIPS");
			return new FeedbackAudioClips(fragmentRootView.findViewById(R.id.view_feedback_audio_clips));
		}
		else {
			return null;
		}
		
	}
	
}
