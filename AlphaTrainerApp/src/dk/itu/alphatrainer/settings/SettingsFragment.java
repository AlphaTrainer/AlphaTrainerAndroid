package dk.itu.alphatrainer.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.SessionManager;


/**
 * 
 * Settings
 * 
 * About settings read more:
 * - http://developer.android.com/guide/topics/ui/settings.html
 *
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = SettingsFragment.class.getName();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate()");
        
		// Load the preferences from an XML resource depending on whether manual mode is enabled
		if (!App.getInstance().manualModeDisabled()) {
			addPreferencesFromResource(R.xml.preferences);
		}
		else {
			addPreferencesFromResource(R.xml.preferences_manual_mode_disabled);
		}
    }
	
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		// if a new headset has been selected, we notify the headset manager
        if (key.equals(getActivity().getString(R.string.key_pref_headset_type))) {
            // App.getInstance().getHeadsetManager().reconnect();
        	App.getInstance().getHeadsetManager().disconnect();
        }
        
        
        // if mode have been change load in the right ui to be selected
        if (key.equals(getActivity().getString(R.string.key_pref_mode))) {
        	
        	ListPreference list = (ListPreference) getPreferenceScreen().findPreference(getActivity().getString(R.string.key_pref_feedback_ui_type));
        	SessionManager mGetSessionManager = App.getInstance().getSessionManager();
        	String mGetAppMode = mGetSessionManager.getAppMode();
        	
        	Log.d(TAG, "getAppMode(): "+ mGetAppMode);
        	
        	// manual mode
        	if (mGetAppMode.equals(getString(R.string.mode_manual))) {        		
        		list.setEntries(R.array.pref_feedback_ui_types_entries_all);
        	    list.setEntryValues(R.array.pref_feedback_ui_types_values_all);
        	    list.setPersistent(true);
        	}

        	// default mode
        	if (mGetAppMode.equals(getString(R.string.mode_default))) {
        		list.setEntries(R.array.pref_feedback_ui_types_entries);
        	    list.setEntryValues(R.array.pref_feedback_ui_types_values);
        	    list.setPersistent(true);
        	    
        	    // now we have to check of current selected ui is in the limited list        	    
        	    boolean flag = false;
        	    for (CharSequence k : list.getEntryValues()) {
        	    	if (k.toString().equals(mGetSessionManager.getFeedbackUiType()))
        	    		flag = true;
        	    } 
        	    if(!flag) {
        	    	Log.d(TAG, "set: " + getActivity().getString(R.string.default_feedback_ui));
        	    	// NIECETOHAVE: this was a bit unstable box is not chosen when looking through feedback ui settings
        	    	mGetSessionManager.setFeedbackUiType(getActivity().getString(R.string.default_feedback_ui));
        	    }
        	            	    
        	}
        	
        }
        
    }
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}

}
