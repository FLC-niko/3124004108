# 论文查重样例测试集

这组文件来自用户提供的 `测试文本.zip`。压缩包中的三个 `del/dis` 文件原本是 GitHub 文件页面的 HTML 保存内容，已提取页面中的 `blob-code` 文本后恢复为 UTF-8 纯文本，避免把网页标签当成论文内容；恢复后只去除了行尾空格，正文字符和换行内容保留。

使用示例：

```bash
cd 3124004108
java -jar main.jar \
  "$(pwd)/examples/orig.txt" \
  "$(pwd)/examples/orig_0.8_add.txt" \
  /tmp/plagiarism-answer.txt
cat /tmp/plagiarism-answer.txt
```

样例文件：

- `orig.txt`：原文；
- `orig_0.8_add.txt`：增加或改写内容的版本；
- `orig_0.8_del.txt`：删除内容的版本；
- `orig_0.8_dis_1.txt`、`orig_0.8_dis_10.txt`、`orig_0.8_dis_15.txt`：调整语序或字符顺序的版本。

这些文件是命令行样例，不会自动并入 `scripts/test.sh` 的 15 个单元测试。
