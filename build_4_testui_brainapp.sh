# create test ui app from scratch:

# NIECETOHAVE: why this first step? is confused about: uitest-project 
# if alone it raises:
# Error: java.io.FileNotFoundException: ./AlphaTrainerAppTestUI/local.properties (No such file or directory)
#
# android create test-project -p ./AlphaTrainerAppTestUI -n AlphaTrainerAppTestUI -m ../AlphaTrainerApp
# android create uitest-project -p ./AlphaTrainerAppTestUI -n AlphaTrainerAppTestUI -t "android-18"

#adb shell uiautomator runtest LaunchSettings.jar -c dk.itu.alphatrainer.ui.LaunchAndroidSettings

android update test-project \
-p ./AlphaTrainerAppTestUI \
-m ../AlphaTrainerApp

cd AlphaTrainerAppTestUI

ant build
adb push ./bin/AlphaTrainerAppTestUI.jar /data/local/tmp/


# NIECETOHAVE: get shell script to loop through an array of <testscases>

adb shell uiautomator runtest AlphaTrainerAppTestUI.jar -c dk.itu.alphatrainer.testsui.LaunchAppSimple

# NIECETOHAVE: enable these when they are adjusted to the final implementation
#adb shell uiautomator runtest AlphaTrainerAppTestUI.jar -c dk.itu.alphatrainer.testsui.LaunchVibrateFeedback
#adb shell uiautomator runtest AlphaTrainerAppTestUI.jar -c dk.itu.alphatrainer.testsui.LaunchCollisionFeedback

#adb shell uiautomator runtest AlphaTrainerAppTestUI.jar -c dk.itu.alphatrainer.LaunchAndroidSettings
