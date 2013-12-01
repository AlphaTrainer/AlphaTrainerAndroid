package dk.itu.alphatrainer.headset;

import android.os.CountDownTimer;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.interfaces.IHeadsetListener;
import dk.itu.alphatrainer.interfaces.IHeadsetManagement;

public class DummyHeadset implements IHeadsetManagement {

	private IHeadsetListener listener;
	private static final int NR_OF_CHANNELS = 1;
	private static final int SAMPLE_RATE = 512;
	private static final int COUNTDOWN_TIMER_CYCLE_LENGTH = 10 * 1000; // 10 seconds
	private static final int TIME_TO_WAIT_BEFORE_UPDATING_CONNECTION_STATUS = 3 * 1000; // 3 seconds
	private boolean dataStreamActive = true;
	
	public DummyHeadset(IHeadsetListener listener) {
		this.listener = listener;
		fakeConnectionStatusUpdates();
	}
	
	private void fakeConnectionStatusUpdates() {
		
		listener.onConnectionStatusUpdate(0);
		
		new CountDownTimer(TIME_TO_WAIT_BEFORE_UPDATING_CONNECTION_STATUS, 500) {
			@Override
			public void onTick(long millisUntilFinished) {
				listener.onConnectionStatusUpdate(TIME_TO_WAIT_BEFORE_UPDATING_CONNECTION_STATUS / (int)millisUntilFinished);
			}
			
			@Override
			public void onFinish() {
				listener.onConnectionStatusUpdate(100);
			}
		}.start();
	}
	
	@Override
	public void startDataStream() {
		
		new CountDownTimer(COUNTDOWN_TIMER_CYCLE_LENGTH, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
				float[] dataChannel = new float[512];
				
				for (int i=0; i<dataChannel.length; i++) {
					dataChannel[i] = (float) Math.random();
				}
				
				// only call listener if datastream is not stopped
				if (dataStreamActive) {
					listener.onDataPacket(1, new float[][]{dataChannel});
				}
			}
			
			@Override
			public void onFinish() {
				// while data stream is still active, we keep calling this method to deliver data
				if (dataStreamActive) {
					startDataStream();
				}
			}
		}.start();
		
	}

	@Override
	public int getNrOfChannels() {
		return NR_OF_CHANNELS;
	}
	
	@Override
	public int getSampleRate() {
		return SAMPLE_RATE;
	}
	
	@Override
	public int getIcon() {
		return R.drawable.headset_dummy_icon_big;
	}

	@Override
	public void stopDataStream() {
		dataStreamActive = false;
	}

}
