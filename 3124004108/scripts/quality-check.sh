#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"
project_dir="$(cd "$script_dir/.." && pwd)"

"$script_dir/build.sh"
git -C "$(cd "$project_dir/.." && pwd)" diff --check
echo "代码质量检查通过：javac -Xlint:all -Werror 与 git diff --check 均无问题。"
