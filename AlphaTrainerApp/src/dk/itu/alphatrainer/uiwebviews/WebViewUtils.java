package dk.itu.alphatrainer.uiwebviews;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

public class WebViewUtils {
	
	/*
	 * Shouldn't be possible to initialize WebViewUtils constructor
	 */
	private WebViewUtils() {}
	
}

final class MyWebChromeClient extends WebChromeClient {
	
	private static final String TAG = MyWebChromeClient.class.getName();
	
	public boolean onConsoleMessage(ConsoleMessage cm) {
		Log.d(TAG, cm.message() + " -- From line " + cm.lineNumber()
				+ " of " + cm.sourceId());
		return true;
	}
}
