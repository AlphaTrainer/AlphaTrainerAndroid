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

public class FeedbackBarGrowing implements IFeedbackUi {

	private final static String TAG = "FeedbackBarGrowing";
	private int growthFactor;
	private View bar;
	private int currentHeight;
	private int screenHeight;
	private static final int TRANSITION_DURATION = 1000;
	
	public FeedbackBarGrowing(View bar) {
		this.bar = bar;
		screenHeight = getScreenHeight();
		growthFactor = screenHeight / 500;
		
		Log.i(TAG, "inside FeedbackBarGrowing(View bar)");
	}
	
	@Override
	public void drawFeedback(int i) {
		
		int growth = i / growthFactor; // * growthFactor;
		
		int heightTo = (growth + currentHeight < screenHeight) ? growth + currentHeight : 0;
		animateHeight(currentHeight, heightTo);
		
		Log.i(TAG, "growth: " + growth + " - currentHeight: " + currentHeight + " - heightTo: " + heightTo);
		
		currentHeight = heightTo;
	}
	
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
