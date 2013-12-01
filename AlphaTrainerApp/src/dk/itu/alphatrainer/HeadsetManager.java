package dk.itu.alphatrainer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.Log;
import dk.itu.alphatrainer.factories.HeadsetFactory;
import dk.itu.alphatrainer.interfaces.IHeadset;
import dk.itu.alphatrainer.interfaces.IHeadsetConnectionStatusListener;
import dk.itu.alphatrainer.interfaces.IHeadsetDataListener;
import dk.itu.alphatrainer.interfaces.IHeadsetListener;
import dk.itu.alphatrainer.interfaces.IHeadsetManagement;
import dk.itu.alphatrainer.settings.ActivitySettings;

/*
 * This class encapsulates connection to and communication with the headset.
 * Connection is determined from number of observers. This class also handles
 * distribution of data from the headset to all registered subscribers.
 */

public class HeadsetManager implements IHeadsetListener { // IHeadsetDataListener, IHeadsetConnectionStatusListener {
	
	private static final String TAG = HeadsetManager.class.getName();
	public static final int CONNECTION_STATUS_UNKNOWN = -1000;
	public static final int CONNECTION_STATUS_IDLE = -1001;
	public static final int CONNECTION_STATUS_CONNECTING = -1002;
	public static final int CONNECTION_STATUS_CONNECTED = -1003;
	public static final int CONNECTION_STATUS_NOT_FOUND = -1004;
	public static final int CONNECTION_STATUS_NOT_PAIRED = -1005;
	public static final int CONNECTION_STATUS_DISCONNECTED = -1006;
	public static final int CONNECTION_ICON_RED = 1;
	public static final int CONNECTION_ICON_YELLOW = 2;
	public static final int CONNECTION_ICON_GREEN = 3;
	
	private static final int INTERVAL_BETWEEN_CONNECTION_ATTEMPTS = 2000; // milliseconds
	private int connectionStatus = CONNECTION_STATUS_UNKNOWN;
	private int connectionIcon = -1;
	private IHeadsetManagement headset;
	private List<IHeadsetDataListener> headsetDataListeners;
	private List<IHeadsetConnectionStatusListener> headsetConnectionStatusListeners;
	private boolean dataStreamActive = false;
	private NotificationManager notificationManager;
	private static final int NOTIFICATION_ID = 123456; // unique number for connection status notification
	
	@SuppressWarnings("static-access")
	public HeadsetManager() {
		headsetDataListeners = new ArrayList<IHeadsetDataListener>();
		headsetConnectionStatusListeners = new ArrayList<IHeadsetConnectionStatusListener>();
		notificationManager = (NotificationManager) App.getInstance().getSystemService(App.getInstance().NOTIFICATION_SERVICE);
	}
	
	public int getConnectionStatus() {
		return connectionStatus;
	}
	
	public IHeadset getHeadset() {
		return headset;
	}
	
	public void subscribeData(IHeadsetDataListener listener) {
		headsetDataListeners.add(listener);
		
		/*
		 * for now we don't start and stop data stream, we keep an active connection whenever the application is loaded
		 *
		if (!dataStreamActive) {
			headset.startDataStream();
			dataStreamActive = true;
		}
		*/
		Log.d(TAG, "subscribe() | listeners: " + headsetDataListeners.size() + " | dataStreamActive: " + dataStreamActive);
	}
	
	public void unsubscribeData(IHeadsetDataListener listener) {
		headsetDataListeners.remove(listener);
		
		/*
		 * for now we don't start and stop data stream, we keep an active connection whenever the application is loaded
		 *
		if (dataStreamActive && headsetListeners.size() <= 0) {
			headset.stopDataStream();
			dataStreamActive = false;
		}
		*/
		Log.d(TAG, "unsubscribe() | listeners: " + headsetDataListeners.size() + " | dataStreamActive: " + dataStreamActive);
	}
	
	
	public void subscribeConnectionStatus(IHeadsetConnectionStatusListener listener) {
		headsetConnectionStatusListeners.add(listener);
		Log.d(TAG, "subscribeConnectionStatus() | listeners: " + headsetConnectionStatusListeners.size());
	}
	
	public void unsubscribeConnectionStatus(IHeadsetConnectionStatusListener listener) {
		headsetConnectionStatusListeners.remove(listener);
		Log.d(TAG, "unsubscribeConnectionStatus() | listeners: " + headsetConnectionStatusListeners.size());
	}
	
	
	/*
	 * distribute data to all subscribers
	 */
	@Override
	public void onDataPacket(int channels, float[][] data) {
		for (IHeadsetDataListener listener : headsetDataListeners) {
			listener.onDataPacket(channels, data);
		}
	}
	
	
	/*
	 * distribute data to all subscribers
	 */
	@Override
	public void onConnectionStatusUpdate(int connectionStatus) {
		this.connectionStatus = connectionStatus;
		dispatchConnectionStatusToListeners();
		
		Log.d(TAG, "onConnectionStatusUpdate() - connectionStatus: " + connectionStatus + " - isConnected(): " + isConnected());
		
		if (isConnected() && connectionStatus == CONNECTION_STATUS_NOT_FOUND) {
			
			new CountDownTimer(INTERVAL_BETWEEN_CONNECTION_ATTEMPTS, INTERVAL_BETWEEN_CONNECTION_ATTEMPTS) {
				
				@Override
				public void onTick(long millisUntilFinished) {					
				}
				
				@Override
				public void onFinish() {
					
					if (isConnected()) {
						Log.d(TAG, "trying again to start datastream");
						headset.startDataStream();
					}
					
				}
			}.start();
			
		}
	}
	
	private void dispatchConnectionStatusToListeners() {
		// since we don't know how soon our headset will call us back, we need to check that our structure holding listeners is initialized
		if (headsetConnectionStatusListeners == null) return; 
		
		for (IHeadsetConnectionStatusListener listener : headsetConnectionStatusListeners) {
			listener.onConnectionStatusUpdate(connectionStatus);
		}
		
		updateNotification(false);
	}
	
	
	public void connect() {
		Log.d(TAG, "connect() - isConnected(): " + isConnected());
		
		if (!isConnected()) {
			headset = HeadsetFactory.getHeadset(this);
			headset.startDataStream();
		}
		else {
			updateNotification(true);
			dispatchConnectionStatusToListeners();
		}
	}
	
	public void reconnect() {
		Log.d(TAG, "reconnect()");
		
		disconnect();
		connect();
	}
	
	public void disconnect() {
		Log.d(TAG, "disconnect()");
		
		connectionStatus = CONNECTION_STATUS_UNKNOWN;
		connectionIcon = -1;
		cancelNotification();
		
		if (isConnected()) {
			headset.stopDataStream();
			headset = null;
		}
	}
	
	public boolean isConnected() {
		return headset != null;
	}
	
	
	private void updateNotification(boolean forceUpdate) {
		int smallIcon;
		int newConnectionIcon;
		String connectionStatusMsg;
		
		if (connectionStatus <= 0) {
			smallIcon = R.drawable.headphones_not_connected; // R.drawable.circle_small_red;
			newConnectionIcon = CONNECTION_ICON_RED;
			connectionStatusMsg = App.getInstance().getString(R.string.status_not_connected);
		}
		else if (connectionStatus >= 100) {
			smallIcon = R.drawable.headphones_connected; // R.drawable.circle_small_green;
			newConnectionIcon = CONNECTION_ICON_GREEN;
			connectionStatusMsg = App.getInstance().getString(R.string.status_good_connection);
		}
		else {
			smallIcon = R.drawable.headphones_not_connected; // R.drawable.circle_small_yellow;
			newConnectionIcon = CONNECTION_ICON_YELLOW;
			connectionStatusMsg = App.getInstance().getString(R.string.status_poor_connection);
		}
		
		if (forceUpdate || newConnectionIcon != connectionIcon) {
			connectionIcon = newConnectionIcon;
			showNotification(smallIcon, connectionStatusMsg);
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void showNotification(int smallIcon, String connectionStatusMsg) {
		
		if (isConnected()) {
		
			Intent intent = new Intent(App.getInstance(), ActivitySettings.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(), 0, intent, 0);
			
			String title = App.getInstance().getString(R.string.notification_title) + " " + connectionStatusMsg;
			String text = App.getInstance().getString(R.string.notification_text);

			Bitmap largeIconBitmap = BitmapFactory.decodeResource(
					App.getInstance().getResources(), R.drawable.alpha_lowercase_icon);
		    
			Notification notification;
			
			if (android.os.Build.VERSION.SDK_INT >= 16) {
			    
				// Build notification
				notification = new Notification.Builder(App.getInstance())
				        .setContentTitle(title)
				        .setContentText(text)
				        .setSmallIcon(smallIcon)
				        .setLargeIcon(largeIconBitmap)
				        .setContentIntent(pendingIntent)
				        .build();
			}
			else {
			    
			    // Set the icon, scrolling text and timestamp
			    notification = new Notification(smallIcon, text, System.currentTimeMillis());
		
			    // Set the info for the views that show in the notification panel.
			    notification.setLatestEventInfo(App.getInstance(), title, text, pendingIntent);
			}
		
		    // Send the notification.
		    notificationManager.notify(NOTIFICATION_ID, notification);
		}
	}
	
	
	public void cancelNotification() {
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
}
