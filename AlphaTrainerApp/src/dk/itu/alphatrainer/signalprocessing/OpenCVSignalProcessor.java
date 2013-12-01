package dk.itu.alphatrainer.signalprocessing;

import android.util.Log;
import dk.itu.alphatrainer.interfaces.ISignalProcessingListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessor;
import dk.itu.alphatrainer.model.AlphaMinMax;

public class OpenCVSignalProcessor implements ISignalProcessor {

	private static final String TAG = "OpenCVSignalProcessor";
	
	// for now we just declare these fixed on the processor 
	private static final float lowCutFq = 5;
	private static final float hiCutFq = 50;
	
	private ISignalProcessingListener listener;

	public OpenCVSignalProcessor(ISignalProcessingListener listener) {
		this.listener = listener;
	}

	@Override
	public void getBandPower(float[][] data, int Fs, float alphaPeak) {
		
		Log.v(TAG,
				"getBandPower() called before the native call to getBrainProcessed(data, data.length) - data: "
						+ data + " data.length: " + data.length);

		listener.onSignalProcessed(this.getBrainProcessed(data[0], data.length, data[0].length, Fs, lowCutFq, hiCutFq, alphaPeak));

	}


	@Override
	public AlphaMinMax getMinMax(float[] alphaPowers) {
		// NIECETOHAVE: add factor to settings - for now it is just 2
		float[] tmp = getMinMaxProcessed(alphaPowers, alphaPowers.length, 2);
		return new AlphaMinMax(tmp[0], tmp[1]);
	}
	
	
	@Override
	public float getAlphaPeak(float[][] data, int Fs) {
		return getAlphaPeak(data[0], data.length, data[0].length, Fs);
	}
	
	
	/**
	 * New method calling native code -jni calling up methods from
	 * dk.itu.alphatrainer_signalprocessing_OpenCVSignalProcessor.cpp
	 * 
	 * */
	public native float getBrainProcessed(float[] eeg, int channels, int samples, int Fs, float lowCutFq, float hiCutFq, float alphaPeak);

	
	/**
	 * New method calling native code -jni calling up methods from
	 * dk.itu.alphatrainer_signalprocessing_OpenCVSignalProcessor.cpp
	 * 
	 * */
	public native float[] getMinMaxProcessed(float[] alphaPowers, int alphaLevelsLength, int factor);	
	
	
	/**
	 * New method calling native code -jni calling up methods from
	 * dk.itu.alphatrainer_signalprocessing_OpenCVSignalProcessor.cpp
	 * 
	 * NIECETOHAVE - change to support multi channels 
	 * */
	public native float getAlphaPeak(float[] eeg, int channels, int samples, int Fs);
	
	/**
	 * Include the new library
	 * 
	 */
	static {
		System.loadLibrary("brainprocesslib");
	}


}
