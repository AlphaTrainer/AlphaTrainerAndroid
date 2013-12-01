package dk.itu.alphatrainer.interfaces;

import dk.itu.alphatrainer.model.AlphaMinMax;

public interface ISignalProcessor {
	public void getBandPower(float[][] data, int Fs, float alphaPeak);
	public float getAlphaPeak(float[][] data, int Fs);
	public AlphaMinMax getMinMax(float[] alphaPowers); 
}
