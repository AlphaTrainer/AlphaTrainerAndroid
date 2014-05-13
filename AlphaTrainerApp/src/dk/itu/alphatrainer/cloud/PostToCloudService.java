package dk.itu.alphatrainer.cloud;

import java.util.List;

import dk.itu.alphatrainer.App;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PostToCloudService extends Service {	

	private static final String TAG = PostToCloudService.class.getName();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public PostToCloudService getService() {
            return PostToCloudService.this;
        }
    }

    @Override
    public void onCreate() {
    	Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Received start id " + startId + ": " + intent);
        
		List<Integer> recordingIds = App.getInstance().getDAO().getRecordingsNotUpdatedToService();
		Log.d(TAG, "Ok we got some recordings that are not uploaded to service yet - recordingIds: "+ recordingIds.toString());
		for (int n : recordingIds) {
			new PostRecordingToCloudTask().execute(n);
		}
		/* NIECETOHAVE: ^^ change the above to make it possible to execute with an array of ints
		if (recordingIds != null){
		new PostRecordingToServiceTask().execute(new int[]{3});
  		new PostRecordingToServiceTask().execute(recordingIds.toArray());
		}*/    
        
		// stop service we are done
        stopSelf();
        
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
    	Log.d(TAG, "onDestroy");       
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();
 
}