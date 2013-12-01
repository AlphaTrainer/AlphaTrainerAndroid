package dk.itu.alphatrainer.ui.feedback.xtra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;

public class FeedbackBar implements IFeedbackUi {

	private final static String TAG = "FeedbackBar";
	private View bar;
	private int currentHeight;
	private int screenHeight;
	private static final int TRANSITION_DURATION = 1000;
	
	public FeedbackBar(View bar) {
		this.bar = bar;
		screenHeight = getScreenHeight();
		Log.i(TAG, "inside FeedbackBar(View bar) - screenHeight " + screenHeight);
	}
	
	@Override
	public void drawFeedback(int i) {
		int heightTo = i * screenHeight / 100;
		animateHeight(currentHeight, heightTo);
	}

	/*
	 * Note: consider a variable duration time as done here (http://stackoverflow.com/questions/4946295/android-expand-collapse-animation)
	 * 
	 * <code>
	 * 1dp/ms
     * a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
     * 
     * 
     * NIECETOHAVE^^
	 */
	private void animateHeight(final int heightFrom, final int heightTo) {
		
		final int difference = heightTo - heightFrom;
		final int offset = difference > 0 ? Math.min(heightFrom, heightTo) : Math.max(heightFrom, heightTo);
		
		Animation heightAnimation = new Animation() {
			
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	bar.getLayoutParams().height = ((int) (interpolatedTime * difference)) + offset;
	            bar.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };
		heightAnimation.setDuration(TRANSITION_DURATION);
		bar.startAnimation(heightAnimation);	
		currentHeight = heightTo;
	}
	
	
	@Override
	public void stop() {
	}
	
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private int getScreenHeight() {
		WindowManager wm = (WindowManager) App.getInstance().getAppContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		if (android.os.Build.VERSION.SDK_INT < 13) {
			return display.getHeight();
		}
		else {
			display.getSize(size);
			return size.y;
		}
		
	}

}
