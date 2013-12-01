package dk.itu.alphatrainer.ui.feedback.xtra;

import android.util.Log;
import android.view.View;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;

public class FeedbackBoxHistory implements IFeedbackUi {

	private final static String TAG = "FeedbackBoxHistory";
	private final int recentHistorySize = 6;
	private View box;
	private View bg;
	private int alphaLevelSum = 0;
	private int alphaLevelsReceived = 0;
	private int[] recentHistory;
	private int currentColor;
	private int currentBgColor;
	private static final int TRANSITION_DURATION = 1000;
	
	public FeedbackBoxHistory(View box, View bg) {
		this.box = box;
		this.bg = bg;
		recentHistory = new int[recentHistorySize];
	}
	
	@Override
	public void drawFeedback(int i) {
		
		// update total and recent history
		recentHistory[alphaLevelsReceived++ % recentHistorySize] = i;
		alphaLevelSum += i;
		
		// feedback box
		int colorTo = UiUtils.AlphaLevelToColor(i);
		UiUtils.animateBackgroundColor(box, currentColor, colorTo, TRANSITION_DURATION);
		currentColor = colorTo;
		
		// background color representing recent history
		int colorBgTo = UiUtils.AlphaLevelToColor(Utils.sumOfIntArray(recentHistory) / Math.min(alphaLevelsReceived, recentHistorySize));
		// if background should represent average of all data received, use line below instead:
		// int colorBgTo = UiUtils.AlphaLevelToColor(alphaLevelSum / alphaLevelsReceived);
		UiUtils.animateBackgroundColor(bg, currentBgColor, colorBgTo, TRANSITION_DURATION);
		currentBgColor = colorBgTo;
	}
	
	@Override
	public void stop() {
	}
	

}
