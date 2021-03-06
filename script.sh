#!/bin/bash
mvn clean package

rm -rf tmp/
mkdir tmp/
mkdir tmp/logs/

cd tmp/

git clone https://github.com/rsommerard/program-repair-test.git

cp -R program-repair-test/ program-repair-test-previous/
cp -R program-repair-test/ program-repair-test-after/
mv program-repair-test/ program-repair-test-current/

cd program-repair-test-current/

mvn clean test 1> ../logs/current-test.log

line=`grep -m 1 -n "^-" ../logs/current-test.log | cut -f 1 -d :`
tail -n +$line ../logs/current-test.log > ../logs/current-test.log.tmp
mv ../logs/current-test.log.tmp ../logs/current-test.log

cd ../program-repair-test-previous/

cp ../../target/program-repair-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../../

git checkout HEAD~1
mvn clean test 1> ../logs/previous-test.log

line=`grep -m 1 -n "^-" ../logs/previous-test.log | cut -f 1 -d :`
tail -n +$line ../logs/previous-test.log > ../logs/previous-test.log.tmp
mv ../logs/previous-test.log.tmp ../logs/previous-test.log

############################
# Java jar processing here #
############################
cd ../../
java -jar program-repair-0.0.1-SNAPSHOT-jar-with-dependencies.jar

cd tmp/program-repair-test-after/

mvn clean test 1> ../logs/after-test.log

line=`grep -m 1 -n "^-" ../logs/after-test.log | cut -f 1 -d :`
tail -n +$line ../logs/after-test.log > ../logs/after-test.log.tmp
mv ../logs/after-test.log.tmp ../logs/after-test.log
