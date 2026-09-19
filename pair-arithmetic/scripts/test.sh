#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
build_dir="$project_dir/build"

rm -rf "$build_dir/classes" "$build_dir/test-classes"
mkdir -p "$build_dir/classes" "$build_dir/test-classes"

javac -encoding UTF-8 -Xlint:all -Werror \
  -d "$build_dir/classes" \
  "$project_dir"/src/main/java/*.java

javac -encoding UTF-8 -Xlint:all -Werror \
  -cp "$build_dir/classes" \
  -d "$build_dir/test-classes" \
  "$project_dir"/src/test/java/*.java

java -cp "$build_dir/classes:$build_dir/test-classes" TestRunner
