package dk.itu.alphatrainer.factories;

import android.content.Context;
import android.util.Log;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.headset.DummyHeadset;
import dk.itu.alphatrainer.headset.MindWaveMobile;
import dk.itu.alphatrainer.interfaces.IHeadsetListener;
import dk.itu.alphatrainer.interfaces.IHeadsetManagement;

/**
 * 
 * Handles instantiation of headset.
 * 
 */
public class HeadsetFactory {

	private final static String TAG = HeadsetFactory.class.getName();
	
	
	/*
	 * Should not be possible to instantiate this class
	 */
	private HeadsetFactory() {}

	/*
	 * Get headset based on user settings
	 * 
	 */
	public static IHeadsetManagement getHeadset(IHeadsetListener listener) {

		Context context = App.getInstance().getBaseContext();

		Log.d(TAG, "getHeadset called - headset type read from user settings - headset type: " + App.getInstance().getSessionManager().getHeadsetType() + "\n");

		
		if (App.getInstance().getSessionManager().getHeadsetType().equals(context.getString(R.string.mindwave_mobile)))
			return new MindWaveMobile(listener);
		
		return new DummyHeadset(listener);
		
	}

}
