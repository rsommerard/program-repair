#!/bin/bash
mvn clean package

rm -rf tmp/
mkdir tmp/
mkdir tmp/logs/

cd tmp/

git clone https://github.com/rsommerard/program-repair-test.git

cd program-repair-test/

cp ../../target/program-repair-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./

mvn clean test 1> ../logs/current-test.log

line=`grep -m 1 -n "^-" ../logs/current-test.log | cut -f 1 -d :`
tail -n +$line ../logs/current-test.log > ../logs/current-test.log.tmp
mv ../logs/current-test.log.tmp ../logs/current-test.log

mvn clean

git checkout HEAD~1
mvn clean test 1> ../logs/previous-test.log

line=`grep -m 1 -n "^-" ../logs/previous-test.log | cut -f 1 -d :`
tail -n +$line ../logs/previous-test.log > ../logs/previous-test.log.tmp
mv ../logs/previous-test.log.tmp ../logs/previous-test.log

############################
# Java jar processing here #
############################
java -jar program-repair-0.0.1-SNAPSHOT-jar-with-dependencies.jar

git checkout master

mvn clean test 1> ../logs/after-test.log

line=`grep -m 1 -n "^-" ../logs/after-test.log | cut -f 1 -d :`
tail -n +$line ../logs/after-test.log > ../logs/after-test.log.tmp
mv ../logs/after-test.log.tmp ../logs/after-test.log
