package dk.itu.alphatrainer.ui.feedback;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.Utils;


/**
 * 
 * Feedback with vibrate 
 *
 */
public class FeedbackVibrate implements IFeedbackUi {
	
	private static final String TAG = FeedbackVibrate.class.getName();
	private Vibrator vibrator;
	private TextView mView;

	public FeedbackVibrate(View view) {
		
		mView = (TextView) view;
		
		vibrator = (Vibrator) App.getInstance().getSystemService(
				Context.VIBRATOR_SERVICE);

	}

	@Override
	public void drawFeedback(int i) {

		Log.d(TAG,
				"drawFeedback(int i) called - i: " + i
						+ " Utils.smoothNumberLowCut(Utils.reverseNormalizeNumber(i, 100, 100), 20): "
						+ Utils.smoothNumberLowCut(Utils.reverseNormalizeNumber(i, 100, 100), 20));
		vibrator.vibrate(Utils.smoothNumberLowCut(Utils.reverseNormalizeNumber(i, 100, 100), 20));

		
		// Update UI 
		UiUtils.simpleTextLoader(mView, i);

		
	}
	
	
	@Override
	public void stop() {
		Log.d(TAG, "stop()");
		
		App.getInstance().playNotificationAndVibrate();
	}
	

}
