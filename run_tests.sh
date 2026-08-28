#!/usr/bin/env bash
# Compiles and runs the unit tests.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(mktemp -d)"
trap 'rm -rf "$BUILD_DIR"' EXIT

javac -d "$BUILD_DIR" \
    $(find "$SCRIPT_DIR/src/main/java" -name '*.java') \
    $(find "$SCRIPT_DIR/src/test/java" -name '*.java')
java -cp "$BUILD_DIR" com.carsim.SimulatorTest
