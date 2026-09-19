#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
build_dir="$project_dir/build"
performance_dir="$project_dir/performance"

bash "$project_dir/scripts/build.sh"
mkdir -p "$build_dir/performance-classes"

javac -encoding UTF-8 -Xlint:all -Werror \
  -cp "$build_dir/classes" \
  -d "$build_dir/performance-classes" \
  "$performance_dir/PerformanceBenchmark.java" \
  "$performance_dir/ProfileReport.java"

java \
  -XX:StartFlightRecording="filename=$performance_dir/performance-recording.jfr,settings=profile,dumponexit=true" \
  -cp "$build_dir/classes:$build_dir/performance-classes" \
  PerformanceBenchmark 10000 10 60 \
  | tee "$performance_dir/benchmark-result.txt"

java -Djava.awt.headless=true -cp "$build_dir/performance-classes" \
  ProfileReport \
  "$performance_dir/performance-recording.jfr" \
  "$performance_dir/performance-profile.png"
