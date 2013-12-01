package dk.itu.alphatrainer.ui.feedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IActivityFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;


/**
 * Fragment feedback collision
 * - based upon a visualization from 3d.js
 * 
 * 
 * Its not 100% clear to us if we should extend Fragment or WebViewFragment 
 * http://developer.android.com/reference/android/webkit/WebViewFragment.html
 * - if we have to use WebViewFragment then we can't use the FeedbackUiFactory approach
 *   because it has a return type, Fragment 
 *
 */
public class FragmentFeedbackCollision extends Fragment {
		
	private static final String TAG = FragmentFeedbackCollision.class.getName();
	WebView mWebView;
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

		Log.d(TAG, "onCreateView()");
		
        // Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_feedback_collision, container, false);
		
		// Define the web view so we can control the life cycles 
		// - the setup / fill in is done in FeedbackCollision.java
 		mWebView = (WebView) root.findViewById(R.id.view_feedback_collision);
		
		// Call back parent activity with this fragments root view
		((IActivityFeedbackUi) getActivity()).onFragmentViewCreated(root);

		// Setup font
		UiUtils.changeFonts((ViewGroup) root.findViewById(R.id.layout_feedback_collision_root));
		
		
        return root;
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView()");		
		mWebView.destroy();
	}

	
}
