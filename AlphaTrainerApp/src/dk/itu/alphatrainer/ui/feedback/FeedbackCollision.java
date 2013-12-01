package dk.itu.alphatrainer.ui.feedback;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;
import dk.itu.alphatrainer.util.CustomWebChromeClient;

/**
 * 
 * Feedback with Collision visual feedback
 * 
 */
public class FeedbackCollision implements IFeedbackUi {

	private static final String TAG = FeedbackCollision.class.getName();
	// private static final String PAGE_PATH = "file:///android_asset/collision/collision.html";
	private static final String PAGE_PATH = "file:///android_asset/collision/collision2.html";
	
	private WebView mWebView;
	private WebSettings wSettings;
	
	private int screenWidth;
	private int screenHeight;
	private String jsFeedback;
	// private String jsInit;
	// private Normalizer mNormalizer = new Normalizer();

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public FeedbackCollision(View view, RelativeLayout root) {
		
		final ProgressDialog progressDialog = UiUtils.createProgressDialog(root.getContext());
		progressDialog.show();

		// Ok lets fill in the web view with the collison visualization
		mWebView = (WebView) view.findViewById(R.id.view_feedback_collision);
		
		// support javascript
		wSettings = mWebView.getSettings();
		wSettings.setJavaScriptEnabled(true);
				
		// big surprice - to set the background color it was not enough to do it in the WebView xml
		mWebView.setBackgroundColor(Color.parseColor("#000000"));
				
		mWebView.setClickable(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		Log.d(TAG, "isHardwareAccelerated(): " + mWebView.isHardwareAccelerated());
		
		if (mWebView.isHardwareAccelerated()) {
			// disable hardware acceleration which might not work well with webviews in general
			mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		// enable logging requires WebChromeClient (here a extended version)
		if(App.getInstance().isDebugable()) {
			mWebView.setWebChromeClient(new CustomWebChromeClient());
		}		


		// Get display size
		Point size = UiUtils.getDisplaySize();
		screenWidth = size.x;
		screenHeight = size.y;

		Log.d(TAG, "screenWidth: " + screenWidth);
		Log.d(TAG, "screenHeight: " + screenHeight);
		
		
		final String jsInit = "javascript:" + "init(w=" + screenWidth + ", h=" + screenHeight + ", numNodes=32, gravity=0.045, minRadius=20)";
		
		/*
		// "numNodes=50,gravity=0.025,minRadius=10"+
		jsInit = "javascript:" + "collision.init(" + "width=" + screenWidth
				+ ",height=" + screenHeight + ","
				+ "numNodes=50,gravity=0.05,minRadius=7" + ")";
		*/
		
		// load and show page 
		mWebView.loadUrl(PAGE_PATH);

		Log.d(TAG, "loaded to webview with: " + PAGE_PATH);

		// ensure page are loaded before doing the initializations
		mWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				mWebView.loadUrl(jsInit);
	            if (progressDialog.isShowing()) {
	            	progressDialog.dismiss();
	            }
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
	public void drawFeedback(int i) { 

		Log.d(TAG, "drawFeedback(int i) called - i: " + i);

		// dispatch feedback to web view
		jsFeedback = "javascript:drawFeedback(" + i + ")";
		mWebView.loadUrl(jsFeedback);
		
		
		
		/*
		Boolean flag = mNormalizer.add(i);

		// we are not happy about this null check but
		// we experienced drawFeedback calls before the webview was finished 
		if(mWebView != null) {
		
			if (flag) {
				// do a reset
				mWebView.loadUrl(jsInit);
			} else {
				jsFeedback = "javascript:collision.feedback(" + i + ")";
				mWebView.loadUrl(jsFeedback);
			}
		
		}
		*/

	}

	@Override
	public void stop() {
	}

	/*
	 * Normalizer for alpha values
	 
	private class Normalizer {
		private int counter;
		private int sum;
		private int average;

		private Normalizer() {
		}

		private boolean add(int i) {
			if (counter > 20) {
				average = sum / counter;
				// rest counter and sum
				counter = 0;
				sum = 0;
				// have we got low values for a while ?
				if (average < 20) {
					Log.d(TAG, "we have had 20 recordings - sum: " + sum
							+ " average: " + average);
					return true;
				}
			} else {
				counter++;
			}
			sum = sum + i;
			return false;
		}
	}
	*/
		
}

