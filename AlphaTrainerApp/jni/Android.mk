LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on


# to prevent android ndk debug ‘__gnu_cxx::_Exit’ has not been declared
# http://stackoverflow.com/questions/7209971/ndk-build-undefined-reference-to-errors-when-statically-linking-to-libxml-a
# dind't solve my problem
# LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

# include the native opencv android lib - tested with 2.4.6:
# include $(HOME)/dev/opencv246-android-sdk/sdk/native/jni/OpenCV.mk
include $(OPENCV_ANDROID_SDK)/native/jni/OpenCV.mk


# get cat lg support
# http://stackoverflow.com/questions/10858055/opencv2-4-with-android-native-activity
# http://stackoverflow.com/questions/12639050/write-to-stdout-in-jni-android-without-android-log-h
# well open gl stuff not needed yet: -lEGL -lGLESv1_CM
LOCAL_LDLIBS    += -llog -landroid 

LOCAL_MODULE    := brainprocesslib

# Since we have source + headers files in an external folder, we need to show where they are. (based upon the cartonifier app)

# include files:
LOCAL_SRC_FILES += dk_itu_alphatrainer_signalprocessing_OpenCVSignalProcessor.cpp
LOCAL_SRC_FILES += ../../../opencvbrain/opencvbrainprocessor.cpp
LOCAL_SRC_FILES += ../../../opencvbrain/testutil.cpp
LOCAL_SRC_FILES += ../../../opencvbrain/printutil.cpp
LOCAL_SRC_FILES += ../../../opencvbrain/signalprocessingutil.cpp


#include headers:
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../opencvbrain


include $(BUILD_SHARED_LIBRARY)
