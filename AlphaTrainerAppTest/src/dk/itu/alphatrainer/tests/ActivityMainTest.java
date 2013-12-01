package dk.itu.alphatrainer.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class dk.itu.alphatrainer.ActivityMainTest \
 * dk.itu.alphatrainer.tests/android.test.InstrumentationTestRunner
 */
public class ActivityMainTest extends ActivityInstrumentationTestCase2<dk.itu.alphatrainer.ActivityMain> {


	private Instrumentation mInstrumentation;
	private dk.itu.alphatrainer.ActivityMain mActivity;

	public ActivityMainTest() {
		// Deprecated:
		// super("com.example.mycameraapp", MainActivity.class);
		super(dk.itu.alphatrainer.ActivityMain.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mInstrumentation = this.getInstrumentation();

		// Needed if we are going into control by keys.
		setActivityInitialTouchMode(false);

		mActivity = getActivity();
	}

	/**
	 * Ensure activity are closed properly down.
	 * 
	 * From http://stackoverflow.com/questions/8846615/android-activity-tests-
	 * testing-restarts
	 */
	protected void tearDown() throws Exception {
		// Finish the Activity off (unless was never launched anyway)
		Activity a = super.getActivity();
		if (a != null) {
			a.finish();
			setActivity(null);
		}
	}

	public void testPreConditions() {

		assertNotNull(mActivity);

	}
	
	// NIECETOHAVE: get this one in shape.
	/*
	 * <p/>
	 * There are some good figures about the life cycles:
	 * http://developer.android.com/training/basics/activity-lifecycle/starting.html
	 */
	/*
	public void testLifeCycles() {

		Activity a = mActivity;

		// Use instrumentation to call the Activity's onPause():
		mInstrumentation.callActivityOnPause(a);

		mInstrumentation.callActivityOnResume(a);

		assertNotNull(a);

		FrameLayout some_view = (FrameLayout) a.findViewById(R.id.some_view);
		assertNotNull(preview);

		}*/
	
	

}

