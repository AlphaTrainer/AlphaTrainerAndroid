package dk.itu.alphatrainer.ui.feedback;

import android.view.View;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;

public class FeedbackBox implements IFeedbackUi {

	private View box;
	private int currentColor;
	private static final int TRANSITION_DURATION = 1000;
	
	public FeedbackBox(View box) {
		this.box = box;
	}
	
	@Override
	public void drawFeedback(int i) {
		int colorTo = UiUtils.AlphaLevelToColor(i);
		UiUtils.animateBackgroundColor(box, currentColor, colorTo, TRANSITION_DURATION);
		currentColor = colorTo;
	}

	
	@Override
	public void stop() {
	}
	

}
