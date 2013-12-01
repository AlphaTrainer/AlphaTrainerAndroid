package dk.itu.alphatrainer.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ActivitySettings extends Activity {

	private static final String TAG = ActivitySettings.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate()");

		// Because we have another load of R.xml.preferences in Settingsfragment don't do it here
		
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
		
	}

}
