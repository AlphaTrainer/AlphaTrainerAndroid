// cstddef to get NULL support
// http://stackoverflow.com/questions/462165/error-null-was-not-declared-in-this-scope
// to solved error: 'NULL' was not declared in this scope NIECETOHAVE: is it sane?
#include <cstddef>
#include <android/log.h>
#include "dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor.h"
#include "opencvbrainprocessor.h"

// log approach >
// from: http://stackoverflow.com/questions/11957282/how-to-send-a-char-array-from-jni-to-android
#define LOG_TAG "OpenCVSignalProcessor"
#define LOG(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
//#define AUDIO_TRACK_BUFFER_SIZE (48)

// NB! support C++ name mangling!
extern "C" {


/*
 * Get Brain / EEG processed
 *
 *
 * If we need to print out the jni array do (NIECETOHAVE: turn into a debug flag etc.):
 *
 * <code>
 *	// lets take a look at the jni transformed array
 *	for (int i = 0; i < samples; i++) {
 *		LOGI("Array of int from java index %d = %f", i, pointerInputArray[i]);
 *	}
 *
 */
JNIEXPORT jfloat JNICALL Java_dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor_getBrainProcessed(
		JNIEnv* env, jobject pThis, jfloatArray inputArray, jint channels, jint samples, jint Fs, jfloat lowCutFq, jfloat hiCutFq, jfloat alphaPeak) {

	LOG("Java_dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor_getBrainProcessed: hello!");

	jfloat* pointerInputArray = env->GetFloatArrayElements(inputArray, 0);
	if (pointerInputArray == NULL) {

		LOGE("ERROR : out of memory error thrown");

		// clean up env
		env->ReleaseFloatArrayElements(inputArray, pointerInputArray, 0);

		// we can't just return NULL / or return because or method has a type it might raise:
		// "error: return-statement with no value, in function"
		// - whats a sane approach ? for now 0.0 - NIECETOHAVE - change back to NULL when solved
		//   http://pastebin.com/rcYWpreG
		return 0.0;
	}

	float result = getBrainProcessed(pointerInputArray, channels, samples, Fs, lowCutFq, hiCutFq, alphaPeak);

	// ok we are done lets clean up
	env->ReleaseFloatArrayElements(inputArray, pointerInputArray, 0);

	LOG("result = %f", result );

	return result;

}

/*
 * Get min max processed
 *
 * jni and arrays:
 *
 * 	int size(10);
 *	 jfloatArray result;
 *	 result = env->NewFloatArray(size);
 *	 if (result == NULL) {
 *	     return NULL;
 *	 }
 *	 // fill a temp structure to use to populate the java int array
 *	 jfloat fill[2];
 *	 for (int i = 0; i < size; i++) {
 *	     fill[i] = i + 7.0; // put whatever logic you want to populate the values here.
 *	 }
 *
 */
JNIEXPORT jfloatArray JNICALL Java_dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor_getMinMaxProcessed
  (JNIEnv* env, jobject pThis, jfloatArray inputArray, jint alphaLevelsLength, jint factor) {

	LOG("Java_dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor_getMinMaxProcessed: hello!");

	jfloat* pointerInputArray = env->GetFloatArrayElements(inputArray, 0);
		if (pointerInputArray == NULL) {

			LOGE("ERROR : out of memory error thrown");

			// clean up env
			env->ReleaseFloatArrayElements(inputArray, pointerInputArray, 0);

			// we can't just return NULL / or return because or method has a type it might raise:
			// "error: return-statement with no value, in function"
			// - whats a sane approach ? for now 0.0 - NIECETOHAVE - change back to NULL when solved
			//   http://pastebin.com/rcYWpreG
			return NULL;
		}

	 int size(2);
	 jfloat minMax[size]; // now we have a {0.0, 0.0}; ready to get set
	 jfloatArray result;
	 result = env->NewFloatArray(size);
 	 getMinMax(pointerInputArray, minMax, alphaLevelsLength, factor);

	 // move from the temp structure to the java structure
	 env->SetFloatArrayRegion(result, 0, size, minMax);
	 return result;

}


/*
 * Get alpha peak
 *
 * If we need to print out the jni array do:
 *
 * <code>
 *
 *  // lets take a look at the jni transformed array
 *  for (int i = 0; i < samples; i++) {
 *		LOG("Array of int from java index %d = %f", i, pointerInputArray[i]);
 *	}
 *
 */
JNIEXPORT jfloat JNICALL Java_dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor_getAlphaPeak(
		JNIEnv* env, jobject pThis, jfloatArray inputArray, jint channels, jint samples, jint Fs) {

	LOG("Java_dk_itu_alphatrainer_calibration_ActivityCalibrate_getAlphaPeak: hello!");

	jfloat* pointerInputArray = env->GetFloatArrayElements(inputArray, 0);
	if (pointerInputArray == NULL) {

		LOGE("ERROR : out of memory error thrown");

		// clean up env
		env->ReleaseFloatArrayElements(inputArray, pointerInputArray, 0);

		// we can't just return NULL / or return because or method has a type it might raise:
		// "error: return-statement with no value, in function"
		// - whats a sane approach ? for now 0.0 - NIECETOHAVE -  change back to NULL when solved
		//   http://pastebin.com/rcYWpreG
		return 0.0;
	}

	float result = getAlphaPeak(pointerInputArray, channels, samples, Fs);

	// ok we are done lets clean up
	env->ReleaseFloatArrayElements(inputArray, pointerInputArray, 0);

	LOG("result alpha peak = %f", result );

	return result;

}





} // end of cpp name mingling
