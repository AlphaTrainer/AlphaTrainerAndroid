package dk.itu.alphatrainer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IHeadsetConnectionStatusListener;

public class FragmentConnectionStatus extends Fragment implements IHeadsetConnectionStatusListener {

	private TextView txtStatus;
	private boolean isDummyHeadset;
	private String dummyHeadsetMsg;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_connection_status, container, false);
		txtStatus = (TextView) view.findViewById(R.id.txt_status);
		
		App.getInstance().getHeadsetManager().subscribeConnectionStatus(this);
		
		isDummyHeadset = App.getInstance().getSessionManager().getHeadsetType().equals(getString(R.string.dummy_headset));
		dummyHeadsetMsg = getString(R.string.dummy_headset_title); 
		
		return view;
	}

	@Override
	public void onConnectionStatusUpdate(int connectionStatus) {
		
		String msg = "";
		
		// Make it 100% clear if the dummy headset is in use 
		if (isDummyHeadset){
			msg += dummyHeadsetMsg+": ";
		}

		if (connectionStatus <= 0) {
			msg += String.valueOf(connectionStatus);
			txtStatus.setBackgroundResource(R.color.Red);
		}
		else if (connectionStatus >= 100) {
			msg += connectionStatus + " %";
			txtStatus.setBackgroundResource(R.color.Green);
		}
		else {
			msg += connectionStatus + " %";
			txtStatus.setBackgroundResource(R.color.Yellow);
		}
		
		txtStatus.setText(msg);
		
	}
	
}
