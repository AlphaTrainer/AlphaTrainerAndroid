package dk.itu.alphatrainer.tests.utils;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.util.Utils;



/* NIECETOHAVE: cover these:
 * java.util.List.Utils.append2Array(float[][] a, float[][] b) -> float[][]
 * java.util.List.Utils.appendArray(float[] a, float[] b)	-> float[]
 * java.util.List.Utils.sumOfIntArray(int[] A) -> int
 * java.util.List.Utils.floatListToArray(List<Float> floatList) -> float[]
 * - and other static methods around...
 */
public class UtilsTest extends TestCase {

	private static final String TAG = UtilsTest.class.getName();
	
	
	
	public UtilsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testSumOfIntArray() {
		
		assertEquals(6, dk.itu.alphatrainer.util.Utils.sumOfIntArray(new int[]{1,2,3}));

	}

	@SmallTest
	public void testReverseNormalizeNumber() {
		
		assertEquals(400, dk.itu.alphatrainer.util.Utils.reverseNormalizeNumber(0, 400, 100));
		assertEquals(200, dk.itu.alphatrainer.util.Utils.reverseNormalizeNumber(50, 400, 100));
		assertEquals(0, dk.itu.alphatrainer.util.Utils.reverseNormalizeNumber(100, 400, 100));
		
	}
	
	@SmallTest
	public void testSmoothNumberLowCut() {
		
		assertEquals(21, Utils.smoothNumberLowCut(21, 20));
		
		assertEquals(20, Utils.smoothNumberLowCut(20, 20));	
		
		assertEquals(0, Utils.smoothNumberLowCut(19, 20));
	
	}
	
	
	
}
