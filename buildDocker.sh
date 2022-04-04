#!/bin/bash

./gradlew clean quarkusBuild -Dquarkus.package.type=uber-jar

docker build -t appliance-control-service:latest .
