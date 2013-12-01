package dk.itu.alphatrainer;

import dk.itu.alphatrainer.util.SystemUiHider;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

@SuppressWarnings("unused")
public class FragmentActivityLifecycleManagementFullscreen extends FragmentActivity {

	
	private static final String TAG = "FragmentActivityLifecycleManagement"; 
	private boolean ignoreFullscreen = false; 
	private Handler mHideHandler; 
	private Runnable mHideRunnable;
	// dont remove this one its required
	private View.OnTouchListener mDelayHideTouchListener; 
	
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	
	@Override
	protected void onPause() {
		App.getInstance().handleAppState(false);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		App.getInstance().handleAppState(true);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		
		
		if (controlsView == null || contentView == null) {
			Log.d(TAG, "controlsView == null: " + (controlsView == null) + " - " + "contentView == null: " + (contentView == null));
			ignoreFullscreen = true;
			return;
		}
		
		fullscreenConfig();

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Log.d(TAG, "onClick() - TOGGLE_ON_CLICK: " + TOGGLE_ON_CLICK + " - ignoreFullscreen: " + ignoreFullscreen);
				
				if (ignoreFullscreen) {
					
					return;
				}
				
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});


	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (ignoreFullscreen) return; 
		
		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	
	
	private void fullscreenConfig() {
	
		Log.i(TAG, "fullscreenConfig() - ignoreFullscreen: " + ignoreFullscreen);
		
		/**
		 * Touch listener to use for in-layout UI controls to delay hiding the
		 * system UI. This is to prevent the jarring behavior of controls going away
		 * while interacting with activity UI.
		 */
		mDelayHideTouchListener = ignoreFullscreen ? null : new View.OnTouchListener() { 
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (AUTO_HIDE) {
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
				return false;
			}
		};
		
		mHideHandler = ignoreFullscreen ? null : new Handler(); 
		mHideRunnable = ignoreFullscreen ? null : new Runnable() { 
			@Override
			public void run() {
				mSystemUiHider.hide();
			}
		};

	}
	
	
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		Log.i(TAG, "mHideHandler is null: " + (mHideHandler == null) + " - mHideRunnable is null: " + (mHideRunnable == null));
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
}
