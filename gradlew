#!/bin/sh
#
# Gradle start script. Uses gradle-wrapper.jar if present; otherwise uses
# .gradle/gradle-8.11.1/bin/gradle (run: ./gradlew bootstrap  once to install).
#
set -e
APP_HOME=$( cd -P "${0%/*}/." > /dev/null && pwd )
WRAPPER_JAR=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
WRAPPER_SHARED_JAR=$APP_HOME/gradle/wrapper/gradle-wrapper-shared-8.11.1.jar
GRADLE_INSTALL=$APP_HOME/.gradle/gradle-8.11.1/bin/gradle

# Prefer full Gradle install if present (no wrapper jar needed)
if [ -x "$GRADLE_INSTALL" ]; then
  exec "$GRADLE_INSTALL" "$@"
fi
if [ -f "$WRAPPER_JAR" ] && [ -f "$WRAPPER_SHARED_JAR" ]; then
  CLASSPATH="$WRAPPER_JAR:$WRAPPER_SHARED_JAR"
  exec java -Dfile.encoding=UTF-8 -Xmx64m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
fi
echo "Missing Gradle. Run: gradle wrapper  (with Gradle installed), or use your IDE to run Gradle."
exit 1
