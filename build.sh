#!/bin/sh
set -e
cd "$(dirname "$0")"
for jar in \
  create-1.21.1-6.0.10.jar \
  aeronautics-1.3.2.jar \
  simulated-1.3.2.jar \
  sable-neoforge-1.21.1-2.0.6.jar \
  sable-companion-common-1.21.1-1.6.0.jar \
  flywheel-1.0.6.jar \
  ponder-1.0.82.jar \
  registrate-1.3.0.jar
do
  if [ ! -f "libs/$jar" ]; then
    echo "ERROR: missing libs/$jar"
    echo "See libs/README.md."
    exit 1
  fi
done
./gradlew build
echo "Build complete: build/libs"
