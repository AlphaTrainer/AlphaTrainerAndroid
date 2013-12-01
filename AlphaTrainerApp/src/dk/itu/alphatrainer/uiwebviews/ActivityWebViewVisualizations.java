package dk.itu.alphatrainer.uiwebviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
 * 
 * 
 * <h2>Streamgraph example</h2>
 * <code>
 * 		//final step load and show 
		String pagePath = "file:///android_asset/streamgraph/streamgraph.html";
		webView.loadUrl(pagePath);
		setContentView(webView);
		
		Log.d(TAG, pagePath + " loaded to webview");

		// Java telling JavaScript to do things
		// well hook this up to a timer etc call every sec 
		// and later on to the input from 
		webView.loadUrl("javascript: transition();");
		
		
		
		
 * <h2>Performance</h2>
 * 
 * With nodes over 100 it turns out to be slow 
 * 
 * There are reported issues with hardware acceleration - this might be an solution to turn if of in the manifest
 * 
 * <code>
 *      <application
        android:name="dk.itu.alphatrainer.App"
        android:hardwareAccelerated="false"
        ...
        >
        <activity
            android:name="dk.itu.alphatrainer.ActivityMain"
            android:hardwareAccelerated="true"
 * 
 * And then enable for others or do it programmatically:
 * 
 * <code>
 *  //honeycomb (3.0) and higher
	if (Build.VERSION.SDK_INT >= 11)
	        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
 * 		
 */
public class ActivityWebViewVisualizations extends Activity {

	private static final String TAG = ActivityWebViewVisualizations.class.getName();
	WebSettings wSettings;
	WebView mWebView;
	int screenWidth;
	int screenHeight;

	
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_web_view_visualization);
		
		mWebView = (WebView) findViewById(R.id.web_view_visualization);
		mWebView.setClickable(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		
		// support javascript
		wSettings = mWebView.getSettings();
		wSettings.setJavaScriptEnabled(true);
		
		// final step load and show page
		String pagePath = "file:///android_asset/collision/collision.html";
		mWebView.loadUrl(pagePath);
		
		Log.d(TAG, "loaded to webview with: "+ pagePath);	
		Log.d(TAG, "isHardwareAccelerated(): "+ mWebView.isHardwareAccelerated());
		 
		
		// NIECETOHAVE: could look up a debug flag and only do it if app are in dev ?
		// Enable logging requires WebChromeClient (here a extended version)
		mWebView.setWebChromeClient(new MyWebChromeClient());

		// Get display size
		Display display = getWindowManager().getDefaultDisplay();
		if(Build.VERSION.SDK_INT < 13) {
			screenWidth = display.getWidth();  // deprecated
			screenHeight = display.getHeight();  // deprecated
	    }
	    else {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;	    	
	    }
		
		Log.d(TAG, "screenWidth: " + screenWidth);
		Log.d(TAG, "screenHeight: " + screenHeight);
		
		// ensure page are loaded before doing the initializations
		// for this we need the WebViewClient
		mWebView.setWebViewClient(new WebViewClient(){

			String js = "javascript:"+
					    "collision.init("+
					    "width="+screenWidth+",height="+screenHeight+","+
					    "numNodes=100,gravity=0.025,minRadius=10"+
					    ")";
			
		    public void onPageFinished(WebView view, String url){   
		        mWebView.loadUrl(js);

		    }           
		});
		
		 // disable scroll/drag on touch - based upon http://goo.gl/czTPXH
		 mWebView.setOnTouchListener(new View.OnTouchListener() {

		    public boolean onTouch(View v, MotionEvent event) {
		      return (event.getAction() == MotionEvent.ACTION_MOVE);
		    }
		  });
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}
}
