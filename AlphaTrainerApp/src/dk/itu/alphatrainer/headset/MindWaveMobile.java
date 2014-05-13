package dk.itu.alphatrainer.headset;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.neurosky.thinkgear.TGDevice;

import dk.itu.alphatrainer.HeadsetManager;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IHeadsetListener;
import dk.itu.alphatrainer.interfaces.IHeadsetManagement;

public class MindWaveMobile implements IHeadsetManagement {

	private static final String TAG = "MindWaveMobile";

	private static final int SAMPLE_RATE = 512;
	private static final int NR_OF_CHANNELS = 1;

	private BluetoothAdapter bluetoothAdapter;
	private TGDevice tgDevice;
	private float[] rawData = new float[SAMPLE_RATE];
	private int rawDataIndex = 0; 

	IHeadsetListener listener;

	
	public MindWaveMobile(IHeadsetListener listener) {
		this.listener = listener;
	}
	
	
	@Override
	public void startDataStream() {
		connectToHeadset();
	}

	@Override
	public int getNrOfChannels() {
		return NR_OF_CHANNELS;
	}

	@Override
	public int getSampleRate() {
		return SAMPLE_RATE;
	}

	private void connectToHeadset() {
		
		// prepare for MindWave bluetooth connection
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null) {
			tgDevice = new TGDevice(bluetoothAdapter, handler);			
			boolean connecting = tgDevice.getState() != TGDevice.STATE_CONNECTING;
			boolean connected = tgDevice.getState() != TGDevice.STATE_CONNECTED;
	    	
			if (connecting && connected) {
	    		tgDevice.connect(true);
	    	}
	    	else {
	    		Log.d(TAG, "connecting: " + connecting + " - connected: " + connected + "\n");
	    	}
		}

	}

	/**
	 * Handles messages from TGDevice
	 * 
	 * Note: 
	 * Android likes this to be static - for now we do some non-static operations setting values in a array etc...
	 * perhaps get rid of this approach when we move into a doing the recording in a service etc...
	 * NIECETOHAVE^^
	 * 
	 */
	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case TGDevice.MSG_STATE_CHANGE:

				int status = HeadsetManager.CONNECTION_STATUS_UNKNOWN;
				
				switch (msg.arg1) {
				case TGDevice.STATE_IDLE:
					Log.i(TAG, "Idle...\n");
					status = HeadsetManager.CONNECTION_STATUS_IDLE;
					break;
				case TGDevice.STATE_CONNECTING:
					Log.i(TAG, "Connecting...\n");
					status = HeadsetManager.CONNECTION_STATUS_CONNECTING;
					break;
				case TGDevice.STATE_CONNECTED:
					Log.i(TAG, "Connected.\n");
					tgDevice.start();
					status = HeadsetManager.CONNECTION_STATUS_CONNECTED;
					break;
				case TGDevice.STATE_NOT_FOUND:
					Log.i(TAG, "Can't find\n");
					status = HeadsetManager.CONNECTION_STATUS_NOT_FOUND;
					break;
				case TGDevice.STATE_NOT_PAIRED:
					Log.i(TAG, "not paired\n");
					status = HeadsetManager.CONNECTION_STATUS_NOT_PAIRED;
					break;
				case TGDevice.STATE_DISCONNECTED:
					Log.i(TAG, "Disconnected mang\n");
					status = HeadsetManager.CONNECTION_STATUS_DISCONNECTED;
					break;
				}
				
				// update headset connection status
				if (status != HeadsetManager.CONNECTION_STATUS_UNKNOWN) {
					listener.onConnectionStatusUpdate(status);
				}
				
				break;

			case TGDevice.MSG_POOR_SIGNAL:
				Log.d(TAG, "Signal strength (0=good, 25=poor, >25=not attached to human): " + msg.arg1 + "\n");
				listener.onConnectionStatusUpdate(Math.max(0, 100-msg.arg1));
				break;

			case TGDevice.MSG_RAW_DATA:
				//Log.d(TAG, "We get raw EEG data: " + msg.arg1 + "\n");
				
				// NIECETOHAVE: keep an eye on this part do we mesh up thread safe etc...
				// and yes there is rooms for improvements for example a proper exception handling
				// or another way to build the data array ....
				if (rawDataIndex >= rawData.length){
					listener.onDataPacket(NR_OF_CHANNELS, new float[][]{rawData});
					rawDataIndex=0;
				}
				
				// NIECETOHAVE: probably get rid of this cast and work with int instead
				rawData[rawDataIndex++] = (float) msg.arg1;
				break;

			case TGDevice.MSG_BLINK:
				//Log.d(TAG, "Blink: " + msg.arg1 + "\n");
				break;

			case TGDevice.MSG_LOW_BATTERY:
				Log.v(TAG, "PoorSignal: Low battery!\n");
				break;

			default:
				break;
			}
		}
	};
	
	
	@Override
	public int getIcon() {
		return R.drawable.headset_mindwave_big;
	}

	@Override
	public void stopDataStream() {
		
		// NICETOHAVE: only stop here when we have an application wide headset
		if (tgDevice != null) { 
			tgDevice.stop();
			tgDevice.close();
		}
		
	}

}
