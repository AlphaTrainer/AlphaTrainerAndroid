package dk.itu.alphatrainer.ui.feedback.xtra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;

public class FeedbackBarHistory implements IFeedbackUi {

	private final static String TAG = "FeedbackBarHistory";
	private final int recentHistorySize = 10;
	private View bar;
	private View avg;
	private View bg;
	private int alphaLevelSum = 0;
	private int alphaLevelsReceived = 0;
	private int[] recentHistory;
	private int currentAvg;
	private int currentHeight;
	private int currentBgColor;
	private int screenHeight;
	private static final int TRANSITION_DURATION = 1000;
	
	public FeedbackBarHistory(View bar, View avg, View bg) {
		this.bar = bar;
		this.avg = avg;
		this.bg = bg;
		recentHistory = new int[recentHistorySize];
		screenHeight = getScreenHeight();
	}
	
	@Override
	public void drawFeedback(int i) {
		
		// update total and recent history
		recentHistory[alphaLevelsReceived++ % recentHistorySize] = i;
		alphaLevelSum += i;
		
		// feedback bar
		int heightTo = i * screenHeight / 100;
		animateBarHeight(currentHeight, heightTo);
		currentHeight = heightTo;

		// recent history line
		int avgTo = Utils.sumOfIntArray(recentHistory) / Math.min(alphaLevelsReceived, recentHistorySize) * screenHeight / 100;
		// If line should represent average of all data received, use line below:
		// int avgTo = alphaLevelSum / alphaLevelsReceived * screenHeight / 100;
		animateAvgLine(currentAvg, avgTo);
		
		// background color representing average of all data received
		int bgColorTo = UiUtils.AlphaLevelToColor(alphaLevelSum / alphaLevelsReceived);
		UiUtils.animateBackgroundColor(bg, currentBgColor, bgColorTo, TRANSITION_DURATION);
		currentBgColor = bgColorTo;
	}
	
	private void animateBarHeight(final int heightFrom, final int heightTo) {
		
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
	
	private void animateAvgLine(final int avgFrom, final int avgTo) {
		
		final int difference = avgTo - avgFrom;
		final int offset = difference > 0 ? Math.min(avgFrom, avgTo) : Math.max(avgFrom, avgTo);
		
		Animation avgLineAnimation = new Animation() {
			
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) avg.getLayoutParams();
	        	params.setMargins(0, 0, 0, ((int) (interpolatedTime * difference)) + offset);
	        	avg.setLayoutParams(params);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };
		avgLineAnimation.setDuration(TRANSITION_DURATION);
		
		avg.startAnimation(avgLineAnimation);	
		currentAvg = avgTo;
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
