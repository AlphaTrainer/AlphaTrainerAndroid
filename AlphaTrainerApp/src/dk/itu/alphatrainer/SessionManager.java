package dk.itu.alphatrainer;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * 
 * Session manager
 * 
 * It holds all getter/setter for preferences and settings (user settings done through ui).
 * 
 * Settings should mainly only have get - set is done through the preferences.xml with a few exceptions.
 *
 */
public class SessionManager {

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;
    
    SharedPreferences settings;
    SharedPreferences.Editor settingsEditor;
    
    Context context;

	private static final String TAG = SessionManager.class.getName();
    private static final String PREF_NAME = "AlphaTrainerAppPref";
    public static final String KEY_ALPHA_PEAK_FQ = "alpha_peak_fq";
    public static final String KEY_TOTAL_START_FQ = "total_start_fq";
    public static final String KEY_TOTAL_END_FQ = "total_end_fq";
    public static final String KEY_ALPHA_LEVEL_MIN = "alpha_level_min";
    public static final String KEY_ALPHA_LEVEL_MAX = "alpha_level_max";
    public static final String KEY_FEEDBACK_BASELINE_DURATION = "feedback_baseline_duration";
    public static final String KEY_CALIBRATION_TIMESTAMP = "calibration_timestamp";
    public static final String KEY_BASELINE_TIMESTAMP = "baseline_timestamp";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PREF_HEADSETTYPE = App.getInstance().getString(R.string.key_pref_headset_type);
    public static final String KEY_PREF_FEEDBACK = App.getInstance().getString(R.string.key_pref_feedback_ui_type);
    public static final String KEY_PREF_LENGTH_OF_TRAINING = App.getInstance().getString(R.string.key_pref_training_length);
    public static final String KEY_PREF_MODE = App.getInstance().getString(R.string.key_pref_mode);
    public static final String KEY_PREF_REVERSE_FEEDBACK = App.getInstance().getString(R.string.key_pref_reverse_feedback);
    public static final float NO_ENTRY_FOUND_FLOAT = -1f;
    private static final int PRIVATE_MODE = 0;
    private static final long NO_ENTRY_FOUND_LONG = -1l;
    private static final String NO_ENTRY_FOUND_STRING = "no_entry_found";
    
    
    /*
     * Constructor
     */
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        preferencesEditor = preferences.edit();
        
        settings = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
		settingsEditor = settings.edit();
        
    }
    
    /*
     * Set alpha peak frequencies
     */
    public void setAlphaPeakFrequency(float peakFq) {
    	preferencesEditor.putFloat(KEY_ALPHA_PEAK_FQ, peakFq);
        preferencesEditor.commit();
    }
    
     
    /*
     * Get alpha peak frequencies
     */
    public float getAlphaPeakFrequency() {
    	return preferences.getFloat(KEY_ALPHA_PEAK_FQ, NO_ENTRY_FOUND_FLOAT);
    }
    
    /*
     * Get feedback baseline duration (in seconds)
     */
    public int getBaselineDuration() {
    	// return getFeedbackDuration();
    	int traingInMinutes = settings.getInt(KEY_PREF_LENGTH_OF_TRAINING, context.getResources().getInteger(R.integer.default_baseline_length_in_minutes));
    	return traingInMinutes*60;
    }
    
   /*
    * Get feedback baseline duration (in seconds)
    * - no set for this one its done through user settings
    */
    public int getFeedbackDuration() {
    	int traingInMinutes = settings.getInt(KEY_PREF_LENGTH_OF_TRAINING, context.getResources().getInteger(R.integer.default_training_length_in_minutes));
    	return traingInMinutes*60;
    }
    
    /*
     * Set total frequencies
     */
    public void setTotalFrequencies(float startFq, float endFq) {
    	preferencesEditor.putFloat(KEY_TOTAL_START_FQ, startFq);
    	preferencesEditor.putFloat(KEY_TOTAL_END_FQ, endFq);
        preferencesEditor.commit();
    }
    
    
    /*
     * Get total frequencies
     */
    public float[] getTotalFrequencies() {
    	float[] totalFrequencies = new float[2];
    	totalFrequencies[0] = preferences.getFloat(KEY_TOTAL_START_FQ, NO_ENTRY_FOUND_FLOAT);
    	totalFrequencies[1] = preferences.getFloat(KEY_TOTAL_END_FQ, NO_ENTRY_FOUND_FLOAT);
        return totalFrequencies;
    }
    
    
    /*
     * Get alpha peak and total frequencies
     */
    public float[] getAlphaPeakAndTotalFrequencies() {
    	float[] alphaPeakAndTotalFrequencies = new float[3];
    	alphaPeakAndTotalFrequencies[0] = preferences.getFloat(KEY_ALPHA_PEAK_FQ, NO_ENTRY_FOUND_FLOAT);
    	alphaPeakAndTotalFrequencies[1] = preferences.getFloat(KEY_TOTAL_START_FQ, NO_ENTRY_FOUND_FLOAT);
    	alphaPeakAndTotalFrequencies[2] = preferences.getFloat(KEY_TOTAL_END_FQ, NO_ENTRY_FOUND_FLOAT);
        return alphaPeakAndTotalFrequencies;
    }
    
    
    /*
     * Set min/max alpha levels
     */
    public void setAlphaLevelMinMax(float min, float max) {
    	preferencesEditor.putFloat(KEY_ALPHA_LEVEL_MIN, min);
    	preferencesEditor.putFloat(KEY_ALPHA_LEVEL_MAX, max);
        preferencesEditor.commit();
    }
    
     
    /*
     * Get alpha peak frequencies
     */
    public float[] getAlphaLevelMinMax() {
    	float[] alphaLevelMinMax = new float[2];
    	alphaLevelMinMax[0] = preferences.getFloat(KEY_ALPHA_LEVEL_MIN, NO_ENTRY_FOUND_FLOAT);
    	alphaLevelMinMax[1] = preferences.getFloat(KEY_ALPHA_LEVEL_MAX, NO_ENTRY_FOUND_FLOAT);
        return alphaLevelMinMax;
    }
    
    
    /*
     * Get headset type
     */
    public String getHeadsetType() {
    	return settings.getString(KEY_PREF_HEADSETTYPE, context.getString(R.string.mindwave_mobile));
    }
    
    
    /*
     * Get feedback ui type
     */
    public String getFeedbackUiType() {
    	return settings.getString(KEY_PREF_FEEDBACK, context.getString(R.string.default_feedback_ui));
    }

    /*
     * Get feedback ui type
     */
	public void setFeedbackUiType(String key) {
		Log.d(TAG, "setFeedbackUiType(String key): " + key);
		settingsEditor.putString(KEY_PREF_FEEDBACK, key);
		settingsEditor.commit();

	}

    
    /*
     * Get app mode
     */
    public String getAppMode() {
    	return settings.getString(KEY_PREF_MODE, context.getString(R.string.mode_default));
    }
    
    
    /*
     * Get app mode
     */
    public boolean getReverseFeedback() {
    	return settings.getBoolean(KEY_PREF_REVERSE_FEEDBACK, false);
    }

    
    /**
     * 
     * Generate an id for the user.
     * 
     * Java UUID approach seems safe enough:
     * 
     * http://stackoverflow.com/questions/1155008/how-unique-is-uuid/
     * 
     * @return unique 64-bit hex String
     */
    public String getUserId() {

    	// if user has no user id yet, generate one
    	if (!preferences.contains(KEY_USER_ID)) {
    		Log.d(TAG, "getUserId(): " + "generating id...");
    		preferencesEditor.putString(KEY_USER_ID, UUID.randomUUID().toString());
            preferencesEditor.commit();
    	}

    	Log.d(TAG, "getUserId(): " + preferences.getString(KEY_USER_ID, NO_ENTRY_FOUND_STRING));
    	return preferences.getString(KEY_USER_ID, NO_ENTRY_FOUND_STRING);
    }
    
    
    /*
     * Set calibration timestamp
     */
    public void setCalibrationTimestamp(long timestamp) {
    	preferencesEditor.putLong(KEY_CALIBRATION_TIMESTAMP, timestamp);
        preferencesEditor.commit();
    }
    
    /*
     * Get calibration timestamp
     */
    public long getCalibrationTimestamp() {
    	return preferences.getLong(KEY_CALIBRATION_TIMESTAMP, NO_ENTRY_FOUND_LONG);
    }
    
    
    /*
     * Set baseline timestamp
     */
    public void setBaselineTimestamp(long timestamp) {
    	preferencesEditor.putLong(KEY_BASELINE_TIMESTAMP, timestamp);
        preferencesEditor.commit();
    }
    
    /*
     * Get calibration timestamp
     */
    public long getBaselineTimestamp() {
    	return preferences.getLong(KEY_BASELINE_TIMESTAMP, NO_ENTRY_FOUND_LONG);
    }
    
}
