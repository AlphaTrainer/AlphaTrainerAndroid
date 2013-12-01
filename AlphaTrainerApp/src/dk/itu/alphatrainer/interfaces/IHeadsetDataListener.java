package dk.itu.alphatrainer.interfaces;

/**
 * This is an interface for components using data from brain interface.
 * 
 * Could also contain:
 * 	onError()
 * 	onConnectionProblem()
 * 	etc etc
 */

public interface IHeadsetDataListener {
	public void onDataPacket(int channels, float[][] data);
}
