# 性能分析记录

`PerformanceBenchmark.java` 会在内存中重复计算一组较长的中英文文本 30 次，用于在 JProfiler、VisualVM 或 JDK 自带工具中观察热点函数。它不是评测入口，不会被 `main.jar` 打包。

运行示例：

```bash
cd 3124004108
bash scripts/build.sh
mkdir -p build/performance-classes
javac -encoding UTF-8 -d build/performance-classes \
  -cp build/classes performance/PerformanceBenchmark.java
java -cp build/classes:build/performance-classes PerformanceBenchmark
```

当前实现的主要耗时应集中在 `TextTokenizer.tokenize` 和 `SimilarityCalculator.cosineSimilarity`。博客需要补充实际使用分析工具生成的截图，不能用手工绘制的图片代替：

```text
[待插入：JProfiler/VisualVM 性能分析图，显示热点函数和耗时]
```
