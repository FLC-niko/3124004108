# 第二周个人编程作业：论文查重

本仓库对应学号目录为 [`3124004108`](./3124004108)。程序使用 Java 编写，入口文件是目录中的 `main.jar`。

## 运行

在 `3124004108` 目录下执行：

```bash
java -jar main.jar <原文绝对路径> <抄袭版绝对路径> <答案绝对路径>
```

例如：

```bash
java -jar main.jar /tmp/orig.txt /tmp/orig_add.txt /tmp/answer.txt
```

答案文件只写入一个保留两位小数的浮点数，范围为 `0.00` 到 `1.00`。

## 构建和测试

项目不依赖 Maven、Gradle 或运行时第三方库，使用 JDK 17 自带的 `javac` 和 `jar` 构建：

```bash
cd 3124004108
bash scripts/build.sh
bash scripts/test.sh
bash scripts/quality-check.sh
```

测试脚本包含 15 个测试用例，覆盖完全相同、完全不同、空文件、增删改、重复词、中英文混合、标点差异、非法参数和文件异常等情况。

## 算法说明

程序先把文本转换为 token：中文字符按单字处理，英文和数字按连续单词处理，并统一英文大小写；空白和标点不参与比较。之后统计两个文本中每个 token 的出现次数，把词频向量的余弦相似度作为重复率：

```text
similarity = dot(original, copy)
             / (length(original) * length(copy))
```

这种实现没有网络访问，也不需要下载词典，适合在评测机上直接编译运行。两个文本都没有有效文字时返回 `1.00`，只有一边为空时返回 `0.00`。

## 目录结构

```text
3124004108/
├── main.jar
├── src/main/java/       # 主程序
├── src/test/java/       # 依赖无关的单元测试
├── scripts/             # 构建、测试和质量检查脚本
├── performance/         # 性能测试程序及截图说明
└── docs/                # PSP 和过程记录
```

博客草稿在仓库外的 `outputs/第二周作业_可编辑草稿.md`，其中个人信息、性能分析截图和测试覆盖率截图保留了待填写位置。
