package dk.itu.alphatrainer.signalprocessing;

import dk.itu.alphatrainer.interfaces.ISignalProcessingListener;
import dk.itu.alphatrainer.interfaces.ISignalProcessor;
import dk.itu.alphatrainer.model.AlphaMinMax;

public class DummySignalProcessor implements ISignalProcessor {


	private ISignalProcessingListener listener;
	
	public DummySignalProcessor(ISignalProcessingListener listener) {
		this.listener = listener;
	}
		
	@Override
	public void getBandPower(float[][] data, int Fs, float alphaPeak) {	
		listener.onSignalProcessed(average(data[0]));
	}
	
	@Override
	public AlphaMinMax getMinMax(float[] alphaPowers) {

		float alphaMin = Float.MAX_VALUE;
		float alphaMax = Float.MIN_VALUE;
		
		for (int i=0; i < alphaPowers.length; i++) {
			float alphaPower = alphaPowers[i];
			if (alphaPower < alphaMin) alphaMin = alphaPower;
			if (alphaPower > alphaMax) alphaMax = alphaPower;
		}
		
		return new AlphaMinMax(alphaMin, alphaMax);
	}

	private static float average(float[] data) {  
	    float sum = 0;

	    for (int i=0; i < data.length; i++) {
	    	sum = sum + data[i]; 
	    }

	    return sum / data.length;

	}

	@Override
	public float getAlphaPeak(float[][] data, int Fs) {
		return (float) (1+(Math.random()*8));
	}
	
}
