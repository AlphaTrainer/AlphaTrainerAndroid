package dk.itu.alphatrainer.uiwebviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import dk.itu.alphatrainer.R;

/*
 * loadData() and loadDataWithBaseURL()
 * - loadUrl works fine as long we have relative paths within the html/js/css
 * 
 * main docs:
 * - http://developer.android.com/guide/webapps/webview.html
 * - http://developer.android.com/reference/android/webkit/WebView.html
 * - http://developer.android.com/guide/webapps/debugging.html
 * 
 * <h2>Note</h2>
 * setWebChromeClient vs. setWebViewClient ? 
 * e.g.: webView.setWebChromeClient(new MyWebChromeClient());
 * WebChromeClient provides more features
 * http://stackoverflow.com/questions/2835556/whats-the-difference-between-setwebviewclient-vs-setwebchromeclient
 * 
 * And apparently its ok to use both within same activity.
 * 
 * "You certainly can use both, they just have different functions. Setting your own custom WebViewClient lets you handle onPageFinished, shouldOverrideUrlLoading, etc., WebChromeClient lets you handle Javascript's alert() and other functions."
 * http://stackoverflow.com/questions/6474768/are-webviewclient-and-webchromeclient-mutually-exclusive
 * 
 */
public class ActivityWebView extends Activity {

	private static final String TAG = ActivityWebView.class.getName();
	
	WebView mWebView;
	WebSettings wSettings;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_web_view );   
		
		mWebView = (WebView) findViewById(R.id.web_view);
		mWebView.setClickable(true);
		
		// support javascript
		wSettings = mWebView.getSettings();
		wSettings.setJavaScriptEnabled(true);
		
		// add the special javascript interface so javascript can call up android
		mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

		// final step load and show page
		String pagePath = "file:///android_asset/examples/simple.html";
		mWebView.loadUrl(pagePath);
		setContentView(mWebView);

		Log.d(TAG, pagePath + " loaded to webview");

		// Enable logging requires WebChromeClient (here a extended version)
		mWebView.setWebChromeClient(new MyWebChromeClient());

		// ensure page are loaded before doing the initializations
		// for this we need the WebViewClient
		mWebView.setWebViewClient(new WebViewClient(){
		    public void onPageFinished(WebView view, String url){   
				// Java telling JavaScript to do things
				mWebView.loadUrl("javascript: callMeFromAndroid();");
		    }           
		});

		


	}

	private class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}

	
}
