package dk.itu.alphatrainer.ui.feedback.xtra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IActivityFeedbackUi;

public class FragmentFeedbackBar extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_feedback_bar, container, false);
        
        // call back parent activity with this fragments root view
     	((IActivityFeedbackUi) getActivity()).onFragmentViewCreated(root);
     	
     	return root;
    }

}
