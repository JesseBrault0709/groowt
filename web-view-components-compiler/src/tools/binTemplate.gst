#/usr/bin/env bash

if [ "\$1" == "--debug" ]; then
    shift
    gradle -q toolsJar && java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8192 -cp build/libs/web-view-components-compiler-tools-0.1.0.jar $mainClassName "\$@"
else
    gradle -q toolsJar && java -cp build/libs/web-view-components-compiler-tools-0.1.0.jar $mainClassName "\$@"
fi
