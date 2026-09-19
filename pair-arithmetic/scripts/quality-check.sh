#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"

bash "$project_dir/scripts/test.sh"
bash "$project_dir/scripts/build.sh"
git -C "$project_dir" diff --check

echo "质量检查通过：编译无警告、测试全部通过、未发现空白错误。"
