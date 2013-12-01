package dk.itu.alphatrainer.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class Utils {

	/*
	 * Shouldn't be possible to initialize Utils constructor
	 */
	private Utils() {}
	
	public static final float DEFAULT_ALPHA_LEVEL_MIN = 0.008f;
	public static final float DEFAULT_ALPHA_LEVEL_MAX = 0.15f;
	public final static long SECOND_MILLIS = 1000;
	public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
	public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
	public final static long DAY_MILLIS = HOUR_MILLIS * 24;
	public final static long WEEK_MILLIS = DAY_MILLIS * 7;
	
	
	public static float[][] append2Array(float[][] a, float[][] b) {
		assert(a.length==b.length && a.length>0);
		float[][] result = new float[a.length][];
		for (int i = 0; i < a.length; i++) {			
			result[i] = appendArray(a[i], b[i]);
		}
		return result;
	}
	
	public static float[] appendArray(float[] a, float[] b) {
		float[] result = new float[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
	public static int sumOfIntArray(int[] A) {
		int sum = 0;
		for(int i = 0; i < A.length; i++){
		  sum += A[i];
		}
		return sum;
	}
	

	/*
	 * Taken from http://stackoverflow.com/questions/4837568/java-convert-arraylistfloat-to-float
	 * - can we do it niecer ....
	 */
	public static float[] floatListToArray(List<Float> floatList) {
		float[] floatArray = new float[floatList.size()];	
		for (int i = 0; i < floatList.size(); i++) {
		    Float f = floatList.get(i);
		    floatArray[i] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}		
		return floatArray;
	}

	public static int reverseNormalizeNumber(int num, int max, int factor) { 
		return max - (max/factor * num);
	}

	
	public static int smoothNumberLowCut(int num, int lowCut) {
		if (num < lowCut) return 0;
		return num;
	}

	
	public static String readFile(String fileNamePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(fileNamePath));
    String result = ""; 
    		
    try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append('\n');
            line = br.readLine();
        }
        result.toString();
    } finally {
        br.close();
    }
    return result;
	}
	
	 public static String readHtml(String remoteUrl) {
	    String out = "";
	    BufferedReader in = null;
	    try {
	        URL url = new URL(remoteUrl);
	        in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str;
	        while ((str = in.readLine()) != null) {
	            out += str;
	        }
	    } catch (MalformedURLException e) { 
	    } catch (IOException e) { 
	    } finally {
	        if (in != null) {
	            try {
	                in.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return out;
	}

	 public static long getMilisecondPassedSinceMidnight() {
			Calendar c = Calendar.getInstance();
			long now = c.getTimeInMillis();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			return now - c.getTimeInMillis();		 
	 }
	 
	 
	public static long getSecondsPassedSinceMidnight() {
		return getMilisecondPassedSinceMidnight() / 1000L;
	}
	
	
}
