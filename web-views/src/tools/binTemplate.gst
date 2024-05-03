#/usr/bin/env bash

../gradlew toolsJar && java -cp build/libs/web-tools.jar:build/libs/web.jar $mainClassName "\$@"
