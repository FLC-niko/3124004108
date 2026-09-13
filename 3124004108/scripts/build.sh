#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"
project_dir="$(cd "$script_dir/.." && pwd)"
build_dir="$project_dir/build"

rm -rf "$build_dir"
mkdir -p "$build_dir/classes"

javac -encoding UTF-8 -Xlint:all -Werror \
  -d "$build_dir/classes" \
  "$project_dir"/src/main/java/*.java

jar --create --file "$project_dir/main.jar" \
  --main-class Main \
  -C "$build_dir/classes" .

echo "已生成 $project_dir/main.jar"
