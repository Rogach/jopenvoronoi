#!/bin/bash

main=$1
MAVEN_OPTS="-XX:-OmitStackTraceInFastThrow $2" mvn compile exec:java -Dmain=${main:-Main}
