ACTIVITY=""

if [ "$1" != "" ]; then
    ACTIVITY="$1"
else
    echo "You can specify a activity to be run e.g.: dk.itu.alphatrainer.settings.ActivitySettings or the main activity will be used though there has to be an intent filter set up... for that activity"
fi

# support ant build
# NB: library files have to be relative
# reset properties or library refs get akward
mv ./AlphaTrainerApp/project.properties /tmp

android update project \
-p ./AlphaTrainerApp -n AlphaTrainerApp \
-l ../libs_external_android/numberpicker \
-t android-18




# lets go into the app
cd ./AlphaTrainerApp

# perhaps do ant clean if we get build errors like 
# "crunch ... BUILD FAILED ... build.xml:653: The following error ..."
# full partly traceback: http://pastebin.com/ENmxSy3F
ant clean 

# nb! remember to install lib(s) as well
ant debug -lib ./libs install

# launch activity 
# - requires the activity to have an intent-filter

if [ "$1" != "" ]; then
    echo $ACTIVITY
    adb shell am start -n "dk.itu.alphatrainer/$ACTIVITY"
else
    adb shell am start -n dk.itu.alphatrainer/dk.itu.alphatrainer.ActivityMain
fi


#
# back
#
cd ..

# log for explicit errors - use https://github.com/JakeWharton/pidcat for logging app.name.package
#adb logcat *:errorxo
#adb logcat -s MainActivity
