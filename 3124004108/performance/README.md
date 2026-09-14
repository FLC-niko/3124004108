# 性能分析记录

`PerformanceBenchmark.java` 只用于性能采样，不会被打进提交的 `main.jar`。它在内存中重复计算两段较长的中英文文本，便于观察查重计算中的热点方法。

运行基准程序：

```bash
cd 3124004108
bash scripts/build.sh
mkdir -p build/performance-classes
javac -encoding UTF-8 -d build/performance-classes \
  -cp build/classes performance/PerformanceBenchmark.java
java -cp build/classes:build/performance-classes PerformanceBenchmark 30
```

最后一个参数是迭代次数。采样时使用了 IntelliJ IDEA 自带的 async-profiler，以 CPU 事件记录 `PerformanceBenchmark 3000` 的调用栈，实际运行结果为 `elapsed_ms=22270.44`。报告页面由 profiler 自动生成，再截取为下面的图片。

![async-profiler CPU 性能分析](performance-profile.png)

在 profiler 页面放大业务方法后，`SimilarityCalculator.calculate` 显示为 `2,201 samples, 96.32%`，`SimilarityCalculator.frequency` 显示为 `523 samples, 22.89%`。从图中的业务方法调用链可以直接看到：

- `SimilarityCalculator.calculate` 负责组织一次完整的相似度计算；
- `TextTokenizer.tokenize` 是占用宽度较大的业务方法，主要开销来自逐字符扫描和 token 创建；
- `SimilarityCalculator.frequency` 出现在词频统计调用路径中，使用哈希表统计 token 次数。

这也和代码的设计相符：文本先被扫描和分词，再统计词频，最后计算余弦相似度。当前实现已经让分词过程对字符只扫描一次，点积阶段只遍历一侧词频表并查询另一侧哈希表，避免了对全部 token 做双重遍历。

这张图是真实运行 async-profiler 后生成的 CPU flame graph，方法名来自实际采样栈，不是手工绘制的数据图。博客中可结合图片说明热点方法和对应的优化思路。
