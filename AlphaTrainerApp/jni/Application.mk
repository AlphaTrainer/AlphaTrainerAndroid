# http://stackoverflow.com/questions/15054666/opencv-on-android-headers-no-such-file-directory
APP_STL := gnustl_static
# http://stackoverflow.com/questions/7412224/ndk-error-with-cstdlib
#APP_STL := cstddef
APP_CPPFLAGS := -frtti -fexceptions
APP_ABI := armeabi-v7a
# should be the same as in the ./AndroidManifest.xml
# TODO: can we read it from a common place
APP_PLATFORM := android-11
