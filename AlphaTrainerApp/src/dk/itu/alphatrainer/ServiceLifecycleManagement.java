package dk.itu.alphatrainer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class ServiceLifecycleManagement extends Service {
	
	private static final String TAG = "ServiceLifecycleManagement";
	private boolean destroyed = false;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	ServiceLifecycleManagement getService() {
            return ServiceLifecycleManagement.this;
        }
    }

    @Override
    public void onCreate() {
        killHeadsetConnectionAfterWaiting();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyed = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();
	
    
    private void killHeadsetConnectionAfterWaiting() {
    	
    	new Thread() {
    		
			public void run() {
				
				for (int i = 0; i <= 5; i++) {
					
					// return from run() if service has been destroyed (which means an activity using the headset has been started)
					if (destroyed) return;
					
					SystemClock.sleep(1000);
					Log.d(TAG, "killHeadsetConnectionAfterWaiting(): - i: " + i);
				}
				
				Log.d(TAG, "killHeadsetConnectionAfterWaiting(): - about to kill headset connection");
				
				App.getInstance().getHeadsetManager().disconnect();
			}
		}.start();
		
    }
	
}
