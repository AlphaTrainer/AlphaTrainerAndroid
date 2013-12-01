=====================
 AlphaTrainerAndroid
=====================


Introduction
============


<TODO - make it short>


Install
=======

We assume all Android tools (ADT) like adb, ndk, etc are setup.

Quick
-----

Simply install the AlphaTrainerApp-debug.apk

::

    $ adb install <path-to-apk>/AlphaTrainerApp-debug.apk


Prerequests
-----------

OpenCV Android SDK 2.4.6 - http://opencv.org/downloads.html - and have it set up
in the build path:

::

    $ ls $OPENCV_ANDROID_SDK
    etc	java	native



Build from scratch
------------------

::

    $ cd <some dir that have a check ou of opencvbrain>
    $ ls 
    AlphaTrainerAndroid	opencvbrain 
    ...
    $ cd AlphaTrainerAndroid
    # step 0: build native lib stand alone 
    $ build_0_opencvbrain.sh
    ...
    # step 1: build native lib into app with ndk
    $ build_1_ndk_brainapp.sh
    # step 2 build and install android app:
    $ build_2_android_brainapp.sh




Credits
=======


Martin Poulsen and Pelle Krøgholt


