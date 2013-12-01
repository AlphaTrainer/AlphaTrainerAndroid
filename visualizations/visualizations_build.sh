RELEASE=0
NAME="collision"

echo "hold your hat: you nede node.js (install with brew) / npm (install with brew) / coffee (install with npm) / less (install with npw) !"

echo "run me: sh visualizations_build.sh collision -r"



if [ "$1" != "" ]; then
    NAME="$1"
else
    echo "provide name to build"
fi

if [ "$2" != "" ]; then
    if [ "$2" == "-r" ]; then RELEASE=1; fi
else
    echo "run with *-r* to release stuff into the android app"
fi


cd "$NAME"

pwd


# now build css and js

less -c "$NAME".less > "$NAME".css
coffee -c "$NAME".coffee > "$NAME".js


cd ..


# release to android device
if (( $RELEASE )); then 

    cp -r "$NAME" /tmp/"$NAME"
    rm /tmp/"$NAME"/*.less
    rm /tmp/"$NAME"/*.coffee
    rm -rf ../AlphaTrainerApp/assets/"$NAME"
    mv /tmp/"$NAME" ../AlphaTrainerApp/assets/"$NAME"
    rm ../AlphaTrainerApp/assets/"$NAME"/d3.v3.js

fi
