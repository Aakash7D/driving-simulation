#!/usr/bin/env bash
# Compiles and launches the Driving Simulation CLI.
# Each invocation compiles fresh into a temporary directory and starts a
# brand new JVM process, so no state (cars, field, etc.) is ever carried
# over from a previous run.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(mktemp -d)"
trap 'rm -rf "$BUILD_DIR"' EXIT

javac -d "$BUILD_DIR" $(find "$SCRIPT_DIR/src/main/java" -name '*.java')
exec java -cp "$BUILD_DIR" com.carsim.Main
