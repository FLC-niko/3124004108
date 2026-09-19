# 结对项目：小学四则运算题目生成器

成员：吴与同、赵海翔  
开发环境：JDK 17，程序没有运行时第三方依赖。

## 直接运行

仓库已提供可执行的 `Myapp.jar`。生成 10 道数值范围在 10 以内的题目：

```bash
java -jar Myapp.jar -n 10 -r 10
```

`-n` 可省略，默认生成 10 道题；`-r` 必须给出。程序会在当前工作目录生成 `Exercises.txt` 和 `Answers.txt`。

批改已有答案：

```bash
java -jar Myapp.jar -e Exercises.txt -a Answers.txt
```

结果写入当前目录的 `Grade.txt`。Windows 下也可以把 `Myapp.jar` 与 `Myapp.bat` 放在同一目录后运行：

```bat
Myapp.bat -n 10 -r 10
```

## 构建与验证

在 `pair-arithmetic` 目录执行：

```bash
bash scripts/build.sh
bash scripts/test.sh
bash scripts/quality-check.sh
bash scripts/stress-test.sh
```

当前自动测试共 21 项。压力测试会生成 10,000 道题，检查交换等价去重、减法非负、除法结果为真分数、运算符数量，随后把生成的答案重新批改一遍。

重新生成 JFR 性能记录和热点图：

```bash
bash scripts/profile.sh
```

## 实现要点

- `Rational` 用 `BigInteger` 保存分子和分母，每次运算后约分，避免浮点误差。
- 题目用表达式树保存，`BinaryExpression` 在构造时计算并缓存结果。
- 减法节点只在左值不小于右值时建立；除法节点只保留结果严格位于 0 和 1 之间的情况。
- `ExpressionKey` 递归表示表达式结构。对 `+`、`×` 的左右子树采用无序比较，其他运算保持顺序，因此能按题目给出的规则去重。
- `ExpressionParser` 在批改时重新计算题目，不依赖原答案文件。

## 目录结构

```text
pair-arithmetic/
├── Myapp.jar                 # 已构建的可运行程序
├── Myapp.bat                 # Windows 启动脚本
├── src/main/java/            # 程序源码
├── src/test/java/            # 21 项自动化测试
├── scripts/                  # 构建、测试、压力测试和性能分析脚本
├── performance/              # JFR 热点图与实测结果
└── docs/                     # PSP、设计和测试说明
```

更详细的设计见 [`docs/设计说明.md`](./docs/设计说明.md)，测试用例见 [`docs/测试说明.md`](./docs/测试说明.md)。
