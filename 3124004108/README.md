# 第二周个人编程作业：论文查重

程序使用 Java 17 编写，入口文件为 `main.jar`。

## 运行

```bash
java -jar main.jar <原文绝对路径> <抄袭版绝对路径> <答案绝对路径>
```

答案文件只写入一个保留两位小数的浮点数，范围为 `0.00` 到 `1.00`。

## 构建和测试

```bash
bash scripts/build.sh
bash scripts/test.sh
bash scripts/quality-check.sh
```

项目不依赖 Maven、Gradle 或运行时第三方库。15 个自动测试覆盖相同文本、不同文本、空文件、增删改、中英文混合、非法参数和文件异常等情况。

## 目录

- `src/main/java/`：主程序
- `src/test/java/`：自动测试
- `examples/`：课程样例测试集
- `scripts/`：构建和测试脚本
- `performance/`：性能测试与截图
- `coverage/`：测试覆盖率报告
- `docs/`：PSP 记录
