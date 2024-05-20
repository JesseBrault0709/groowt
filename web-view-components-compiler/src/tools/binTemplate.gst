#/usr/bin/env bash

../gradlew toolsJar && java -cp build/libs/web-tools-0.1.0.jar:build/libs/web-views-0.1.0.jar $mainClassName "\$@"
