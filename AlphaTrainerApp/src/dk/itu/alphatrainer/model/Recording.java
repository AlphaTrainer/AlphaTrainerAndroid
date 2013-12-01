package dk.itu.alphatrainer.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * 
 * Recording model
 * - lets aim to be atomic and reduce setters if possible do it all on the constructor.
 * 
 * Fields needs to be serialized to be build right with Gson - read more in Recording.java
 * 
 * <ul>
 * <li>
 * annotate fields with @Expose that should used in the Gson builder
 * <li>
 * set naming with @SerializedName annotation we use the c style JSON naming
 * convention do the annotation even if the Java field has a simple naming
 * "type" just in case we do some Java refactoring later on.
 */
public class Recording {

	public static final String TYPE_BASELINE = "Baseline";
	public static final String TYPE_FEEDBACK = "Feedback";
	// no expose
	private int id;
	@Expose
	@SerializedName("user_id")
	private String userId;
	@Expose
	@SerializedName("type")
	private String type;
	@Expose
	@SerializedName("min_alpha_level")
	private float minAlphaLevel;
	@Expose
	@SerializedName("max_alpha_level")
	private float maxAlphaLevel;
	@Expose
	@SerializedName("alpha_peak_fq")	
	private float alphaPeakFq;
	@Expose
	@SerializedName("average_alpha_level")
	private float averageAlphaLevel;
	// no expose 
	private int alphaLevelStartId;
	// no expose	
	private int alphaLevelEndId;
	@Expose
	@SerializedName("length")
	private int length;
	@Expose
	@SerializedName("time_stamp_start")
	private long timeStampStart;
	@Expose
	@SerializedName("time_stamp_end")
	private long timeStampEnd;
	@Expose
	@SerializedName("feedback_ui_type")
	private String feedbackUiType;
	@Expose
	@SerializedName("headset_type")
	private String headsetType;
	@Expose
	@SerializedName("alpha_levels")
	private List<AlphaLevel> alphaLevels;

	public Recording(int id, String type, float minAlphaLevel,
			float maxAlphaLevel, float alphaPeakFq, float averageAlphaLevel,
			int alphaLevelStartId, int alphaLevelEndId, String userId, int length, 
			long timeStampEnd, String feedbackUiType, String headsetType) {
		this.id = id;
		this.type = type;
		this.minAlphaLevel = minAlphaLevel;
		this.maxAlphaLevel = maxAlphaLevel;
		this.alphaPeakFq = alphaPeakFq;
		this.averageAlphaLevel = averageAlphaLevel;
		this.alphaLevelStartId = alphaLevelStartId;
		this.alphaLevelEndId = alphaLevelEndId;
		this.userId = userId;
		this.length = length;		
		this.timeStampStart = timeStampEnd - length;
		this.timeStampEnd = timeStampEnd;
		this.feedbackUiType = feedbackUiType;
		this.headsetType = headsetType;
		
		alphaLevels = new ArrayList<AlphaLevel>();
	}

	public int getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getType() {
		return type;
	}

	public float getMinAlphaLevel() {
		return minAlphaLevel;
	}

	public float getMaxAlphaLevel() {
		return maxAlphaLevel;
	}

	public float getAlphaPeakFq() {
		return alphaPeakFq;
	}

	public float getAverageAlphaLevel() {
		return averageAlphaLevel;
	}
	
	public int getAlphaLevelStartId() {
		return alphaLevelStartId;
	}
	
	public int getAlphaLevelEndId(){
		return alphaLevelEndId;
	}

	public int getLength(){
		return length;
	}
	

	public long getTimeStampStart(){
		return timeStampStart;
	}	
	
	public long getTimeStampEnd(){
		return timeStampEnd;
	}

	public String getFeedbackUiType(){
		return feedbackUiType;
	}
	
	public String getHeadsetType(){
		return headsetType;
	}	
	
	
	public void addAlphaLevel(AlphaLevel al) {
		alphaLevels.add(al);
	}

	public int[] getNormalizedAlphaLevels() {
		int[] normalizedAlphaLevels = new int[alphaLevels.size()];
		for (int i = 0; i < alphaLevels.size(); i++) {
			normalizedAlphaLevels[i] = alphaLevels.get(i)
					.getNormalizedAlphaLevel();
		}
		return normalizedAlphaLevels;
	}

	public float[] getAlphaLevels() {
		float[] rawAlphaLevels = new float[alphaLevels.size()];
		for (int i = 0; i < alphaLevels.size(); i++) {
			rawAlphaLevels[i] = alphaLevels.get(i).getAlphaLevel();
		}
		return rawAlphaLevels;
	}

	public List<Number> getAlphaLevelsForXYPlot() {
		List<Number> rawAlphaLevels = new ArrayList<Number>();
		for (int i = 0; i < alphaLevels.size(); i++) {
			rawAlphaLevels.add(i, alphaLevels.get(i).getAlphaLevel());
		}
		return rawAlphaLevels;
	}

}
