package dk.itu.alphatrainer;

import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import dk.itu.alphatrainer.datastorage.DAO;


/*
 * This class is used as an application wide singleton, it has the
 * 
 * In order to work, it is required that we provide a name attribute in AndroidManifest's application tag:
 * <application
 *     ...
 *     android:name="dk.itu.alphatrainer.App" >
 *     ...
 * </application>
 */
public class App extends Application {

	private static final String TAG = App.class.getName();
	private static final boolean MANUAL_MODE_DISABLED = false;
	private static final boolean USE_TEST_DATABASE = false;
	private static App sInstance;
	private static Context appContext;
	private static Date date = new Date();
	private static SessionManager sessionManager;
	private static DAO dao;
	private HeadsetManager headsetManager;
	@SuppressWarnings("unused") // but needed
	private ServiceLifecycleManagement serviceLifecycleManagement;
	private boolean serviceLifecycleManagementIsBound = false;
	private SoundPlayer soundPlayer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		sInstance.initializeInstance();
	}
	
	/*
	 * Initialize session values
	 */
	protected void initializeInstance() {
	    appContext = this.getApplicationContext();
	    sessionManager = new SessionManager(appContext);
	    headsetManager = new HeadsetManager();
	    dao = new DAO(appContext);
	    soundPlayer = new SoundPlayer();
		soundPlayer.initSounds(appContext);
	    
	    // startService(new Intent(this, ServiceLifecycleManagement.class));
	    // doBindService();
	}
	
	/*
	 * get application "singleton" object 
	 */
	public static App getInstance() {
		return sInstance;
	}
	
	
	/*
	 * get headset manager
	 */
	public HeadsetManager getHeadsetManager() {
	    return headsetManager;
	}
	
	/*
	 * get session manager
	 */
	public SessionManager getSessionManager() {
	    return sessionManager;
	}
	
	/*
	 * get dao
	 */
	public DAO getDAO() {
	    return dao;
	}
	
	/*
	 * get application context
	 */
	public Context getAppContext() {
	    return appContext;
	}

	
	/*
	 * get Date()
	 */
	public Date getDate() {
	    return date;
	}
	
	/*
	 * Is app in debug mode/build
	 * 
	 * There are various approaches:
	 * - http://stackoverflow.com/questions/7022653/how-to-check-programmatically-whether-app-is-running-in-debug-mode-or-not
	 * - http://stackoverflow.com/questions/7085644/how-to-check-if-apk-is-signed-or-debug-build
	 * NIECETOHAVE: ^^ clear out
	 */
	public boolean isDebugable(){
	   return  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
	}
	
	
	public boolean manualModeDisabled() {
		return MANUAL_MODE_DISABLED;
	}
	
	public boolean useTestDatabase() {
		return USE_TEST_DATABASE;
	}
	
	/*
	 * this method handles reactions to state changes
	 */
	public void handleAppState(boolean start) {
		
		// well seems hard to use isFinishing since that is false when listing running apps and true when switching between activities
		Log.d(TAG, "handleAppState() - start: " + start);
		
		if (start) {
			doUnbindService();
			headsetManager.connect();
		}
		else {
			doBindService();
			headsetManager.cancelNotification();
		}
		
	}

	
	/*
	 * connection to service for handling connection and disconnection to headset
	 */
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	serviceLifecycleManagement = ((ServiceLifecycleManagement.LocalBinder)service).getService();

	        // Tell the user about this for our demo.
	    	Log.d(TAG, "ServiceLifecycleManagement connected");
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	serviceLifecycleManagement = null;
	    	Log.d(TAG, "ServiceLifecycleManagement disconnected");
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(getAppContext(), 
	    		ServiceLifecycleManagement.class), serviceConnection, Context.BIND_AUTO_CREATE);
	    serviceLifecycleManagementIsBound = true;
	}

	void doUnbindService() {
	    if (serviceLifecycleManagementIsBound) {
	        // Detach our existing connection.
	        unbindService(serviceConnection);
	        serviceLifecycleManagementIsBound = false;
	    }
	}
	
	
	
	/*
	 * method and class for notification sounds
	 */
	public void playNotificationAndVibrate() {
		
		final int VIBRATION_DURATION = 200;
		
		// play notification sound
		soundPlayer.playSound(this, SoundPlayer.CALIBRATION_DONE);
		
		// vibrate
		((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATION_DURATION);
	}
	
	@SuppressLint("UseSparseArrays")
	private class SoundPlayer {

		public static final int CALIBRATION_DONE = R.raw.calibration_done;
		
		private SoundPool soundPool;
		private HashMap<Integer, Integer> soundPoolMap;

		/** Populate the SoundPool*/
		public void initSounds(Context context) {
			soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
			// NIECETOHAVE: change into sparse array
			soundPoolMap = new HashMap<Integer, Integer>();
	
			soundPoolMap.put( CALIBRATION_DONE, soundPool.load(context, CALIBRATION_DONE, 1) );
		}
		
		public void playSound(Context context, int soundID) {
			
			if(soundPool == null || soundPoolMap == null){
				initSounds(context);
			}
			
			float volume = 1.0f; // whatever in the range = 0.0 to 1.0
			
			// play sound with same right and left volume, with a priority of 1,
			// zero repeats (i.e play once), and a playback rate of 1f
			soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);	
		}
		
	}

}
