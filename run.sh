#!/bin/bash

if [[ "$1" == "collect" ]]; then
    MAVEN_OPTS="-XX:-OmitStackTraceInFastThrow" mvn compile exec:java -Dmain=BugHunter -Dexec.args=collect
elif [[ "$1" == "reclassify" ]]; then
    MAVEN_OPTS="-XX:-OmitStackTraceInFastThrow -ea" mvn -Pdumb-run verify -Dmain=BugHunter -Dexec.args=reclassify
else
    MAVEN_OPTS="-XX:-OmitStackTraceInFastThrow $2" mvn -Pdumb-run verify -Dmain=${1:-Main}
fi
