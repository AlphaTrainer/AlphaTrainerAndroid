package dk.itu.alphatrainer.util;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

/**
 * 
 * A custom WebChromeClient
 * 
 * - it enables console.log to Android Log
 *
 */
public final class CustomWebChromeClient extends WebChromeClient {
	
	private static final String TAG = CustomWebChromeClient.class.getName();
	
	public boolean onConsoleMessage(ConsoleMessage cm) {
		Log.d(TAG, cm.message() + " -- From line " + cm.lineNumber()
				+ " of " + cm.sourceId());
		return true;
	}

}




