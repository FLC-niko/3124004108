#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
build_dir="$project_dir/build"
performance_dir="$project_dir/performance"

bash "$project_dir/scripts/build.sh"
mkdir -p "$build_dir/performance-classes" "$build_dir/stress-output"

javac -encoding UTF-8 -Xlint:all -Werror \
  -cp "$build_dir/classes" \
  -d "$build_dir/performance-classes" \
  "$performance_dir/StressValidation.java"

java -cp "$build_dir/classes:$build_dir/performance-classes" \
  StressValidation \
  "$build_dir/stress-output" \
  "$performance_dir/stress-test-result.txt"
