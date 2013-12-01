# go to app
cd ./AlphaTrainerApp


#
# ndk build
# 

# set jni to be more verbose: - if its an emulator its:
# adb shell setprop dalvik.vm.checkjni true
adb shell stop
adb shell setprop debug.checkjni 1
adb shell start


# options:
# - How to force the display of build commands: Do "ndk-build V=1"
# - How to force a rebuild of all your sources: Use GNU Make's "-B" option, as in: ndk-build -B
# more in the ndk docu.. # ~/ndk-r8e/documentation.html
~/dev/ndk-r9/ndk-build V=1 -B


echo "is lib there?:"

tree libs

echo "is tmp files created during compilation there there?:"

tree obj/local

#
# back
#
cd ..
