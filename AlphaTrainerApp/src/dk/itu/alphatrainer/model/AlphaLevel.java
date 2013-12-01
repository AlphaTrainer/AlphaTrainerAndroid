package dk.itu.alphatrainer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Alpha level model
 * 
 * Fields needs to be serialized to be build right with Gson - read more in Recording.java
 */
public class AlphaLevel {

	@Expose
	@SerializedName("alpha_level")
	private float alphaLevel;
	@Expose
	@SerializedName("normalized_alpha_level")
	private int normalizedAlphaLevel;
	@Expose
	@SerializedName("time_stamp")
	private long timeStamp;

	public AlphaLevel(float alphaLevel, int normalizedAlphaLevel, long timeStamp) {
		this.alphaLevel = alphaLevel;
		this.normalizedAlphaLevel = normalizedAlphaLevel;
		this.timeStamp = timeStamp;  
	}

	public float getAlphaLevel() {
		return alphaLevel;
	}

	public int getNormalizedAlphaLevel() {
		return normalizedAlphaLevel;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	
	

}
