package dk.itu.alphatrainer.feedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.FragmentActivityLifecycleManagementFullscreen;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.factories.FeedbackUiFactory;
import dk.itu.alphatrainer.interfaces.IActivityFeedbackUi;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;


public class ActivityFeedback extends FragmentActivityLifecycleManagementFullscreen implements IActivityFeedbackUi {

	private static final String TAG = ActivityFeedback.class.getName();
	private FeedbackLogic feedbackLogic;
	private Button btnStop;
	
	/*
	 * Oncreate - behind the scene a feedback fragment is set up based upon the settings
	 * 
	 * We have a null check because: "if not null is fragment already added (i.e. by configuration change?)" -
	 * this is discussed @: http://stackoverflow.com/questions/5159982/how-do-i-add-a-fragment-to-an-activity-with-a-programmatically-created-content-v  
	 * <pre> 
	 * if (savedInstanceState == null) {
	 * 
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feedback);
		super.onCreate(savedInstanceState);
		
		// setup stop button - appears when touching the screen, otherwise it is hidden
		btnStop = (Button) findViewById(R.id.btn_stop);
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish(); // resembles a push on the back button
			}
		});
		
		if (savedInstanceState == null) {
			
			Log.d(TAG, "adding fragment ...");
			
			View root = findViewById(R.id.fullscreen_content);
			Fragment f = FeedbackUiFactory.getFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(root.getId(), f).commit();
           
        }
		
	}

	@Override
	public void onFragmentViewCreated(View fragmentRootView) {

		IFeedbackUi feedbackUi = FeedbackUiFactory.getFeedbackUi(fragmentRootView);
		
		feedbackLogic = new FeedbackLogic(feedbackUi, App.getInstance().getSessionManager().getFeedbackDuration(), false);
		
	}

	@Override
	protected void onPause() {
		// when application is sent to background, stop feedback and finish this activity
		feedbackLogic.stop();
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
	

}
