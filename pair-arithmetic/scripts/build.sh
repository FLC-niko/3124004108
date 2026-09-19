#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
build_dir="$project_dir/build"

rm -rf "$build_dir/classes"
mkdir -p "$build_dir/classes"

javac -encoding UTF-8 -Xlint:all -Werror \
  -d "$build_dir/classes" \
  "$project_dir"/src/main/java/*.java

jar --create --file "$project_dir/Myapp.jar" \
  --main-class Main \
  -C "$build_dir/classes" .

echo "构建完成：$project_dir/Myapp.jar"
