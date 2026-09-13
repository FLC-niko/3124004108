#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"
project_dir="$(cd "$script_dir/.." && pwd)"
build_dir="$project_dir/build"

rm -rf "$build_dir/test-classes"
mkdir -p "$build_dir/test-classes"

"$script_dir/build.sh"

javac -encoding UTF-8 -Xlint:all -Werror \
  -cp "$build_dir/classes" \
  -d "$build_dir/test-classes" \
  "$project_dir"/src/test/java/*.java

java -cp "$build_dir/classes:$build_dir/test-classes" TestRunner
