package dk.itu.alphatrainer.ui.feedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IActivityFeedbackUi;
import dk.itu.alphatrainer.ui.UiUtils;

public class FragmentFeedbackVibrate extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_feedback_vibrate, container, false);
		
		// Call back parent activity with this fragments root view
		((IActivityFeedbackUi) getActivity()).onFragmentViewCreated(root);
		
		// Setup font
		UiUtils.changeFonts((ViewGroup) root.findViewById(R.id.layout_feedback_vibrate_root));
		
        return root;
    }
	
	
}
