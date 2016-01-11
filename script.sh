#!/bin/bash

rm -rf tmp/
mkdir tmp/
mkdir tmp/logs/

cd tmp/

git clone https://github.com/rsommerard/program-repair-test.git

cd program-repair-test/
mvn clean test 1> ../logs/current-test.log

mvn clean

git checkout HEAD~1
mvn clean test 1> ../logs/previous-test.log

git checkout master

############################
# Process logs here        #
############################



############################
# Java jar processing here #
############################



mvn clean test 1> ../logs/after-test.log
