package dk.itu.alphatrainer.model;

public class AlphaMinMax {

	private float alphaMin;
	private float alphaMax;
	
	public AlphaMinMax(float alphaMin, float alphaMax) {
		this.alphaMin = alphaMin;
		this.alphaMax = alphaMax;  
	}

	public float getAlphaMin() { 
		return alphaMin;
	}

	public float getAlphaMax() {
		return alphaMax;
	}
	
}
