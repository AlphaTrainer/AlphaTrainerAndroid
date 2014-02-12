# non rooting approach to pull db - requires app in debug mode
adb -d shell 'run-as dk.itu.alphatrainer chmod 666 \
cat /data/data/dk.itu.alphatrainer/databases/AlphaTrainerAppDB > /sdcard/AlphaTrainerAppDB.sql'
adb -d pull /sdcard/AlphaTrainerAppDB.sql /tmp/
ls /tmp/AlphaTrainerAppDB.sql
sqlite3 /tmp/AlphaTrainerAppDB.sql


# another way to extract sqlite database from phone
# tool_dump_sqldatabase.sh script gave me error 
#    > run-as: Package 'dk.itu.alphatrainer' is unknown

# request backup - triggers a promt on screen, DO NOT password protect
# > adb backup -f /tmp/data.ab -noapk dk.itu.alphatrainer

# extract backed up data, result is the folder apps/dk.itu.alphatrainer/
# > dd if=data.ab bs=1 skip=24 | python -c "import zlib,sys;sys.stdout.write(zlib.decompress(sys.stdin.read()))" | tar -xvf -

