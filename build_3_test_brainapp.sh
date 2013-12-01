INSTRUMENTATION=0
UITEST=0

if [ "$1" != "" ]; then
    if [ "$1" == "-i" ]; then INSTRUMENTATION=1; fi
    if [ "$1" == "-ui" ]; then UITEST=1; fi
else
    echo "Run several tests with instrumentation on a real device use parameter: *-i* and ensure to have a device connected - use -ui to get seperate ui tests..."
fi


# create test app from scratch:
# android create test-project -p ./AlphaTrainerAppTest -n AlphaTrainerAppTest -m ../AlphaTrainerApp

# ensure ant.properties are:
# tested.project.dir=../AlphaTrainerApp

# set app properties

android update test-project \
-p ./AlphaTrainerAppTest \
-m ../AlphaTrainerApp



cd AlphaTrainerAppTest



# build / install - it also build / install AlphaTrainerApp
ant debug -lib ./libs install


# run test with instrumentation -> run test on device
if (( $INSTRUMENTATION )); then 

    adb shell am instrument -w \
    -e class dk.itu.alphatrainer.tests.ActivityMainTest \
    dk.itu.alphatrainer.tests/android.test.InstrumentationTestRunner

fi


#
# run all tests locally with ant
#
ant test


#
# uiautomator test
#
if (( $UITEST )); then 
    cd ..
    sh build_4_testui_brainapp.sh
fi
