#!/bin/bash

main=$1
MAVEN_OPTS="-XX:-OmitStackTraceInFastThrow" mvn compile exec:java -Dmain=${main:-Main}
