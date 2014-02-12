# assuming opencvbrain cloned/placed in the container dir of AlphaTrainerAndroid 

mkdir -p ../opencvbrain/build

cd ../opencvbrain/build

cmake ..

make

./opencvbrain

cd ../../..
