import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 对需求中的一万题规模做完整生成、落盘和回读批改。 */
public final class StressValidation {
    private StressValidation() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("用法：StressValidation <临时输出目录> <报告文件>");
        }
        Path outputDirectory = Path.of(args[0]);
        Path reportFile = Path.of(args[1]);
        Files.createDirectories(outputDirectory);

        long start = System.nanoTime();
        List<Expression> expressions = new ExpressionGenerator().generate(10_000, 10);
        validate(expressions);
        long generated = System.nanoTime();

        ExerciseFiles.writeExercises(expressions, outputDirectory);
        GradeResult result = new AnswerGrader().grade(
                outputDirectory.resolve("Exercises.txt"),
                outputDirectory.resolve("Answers.txt"),
                outputDirectory.resolve("Grade.txt"));
        long finished = System.nanoTime();

        if (result.correct().size() != 10_000 || !result.wrong().isEmpty()) {
            throw new AssertionError("生成答案回读批改失败");
        }

        String report = String.format(
                "一万题压力测试通过%n"
                        + "题目数：10,000%n"
                        + "等价去重后数量：10,000%n"
                        + "约束检查：运算符数量、减法非负、除法真分数均通过%n"
                        + "生成耗时：%.3f 秒%n"
                        + "写入并回读批改耗时：%.3f 秒%n"
                        + "批改结果：Correct 10,000，Wrong 0%n",
                (generated - start) / 1_000_000_000.0,
                (finished - generated) / 1_000_000_000.0);
        Files.writeString(reportFile, report, StandardCharsets.UTF_8);
        System.out.print(report);
    }

    private static void validate(List<Expression> expressions) {
        Set<ExpressionKey> keys = new HashSet<>();
        for (Expression expression : expressions) {
            if (expression.operatorCount() > 3) {
                throw new AssertionError("出现超过 3 个运算符的题目");
            }
            if (!keys.add(expression.equivalenceKey())) {
                throw new AssertionError("出现交换 + 或 × 后等价的重复题目");
            }
            validateNode(expression);
        }
    }

    private static void validateNode(Expression expression) {
        if (!(expression instanceof BinaryExpression binary)) {
            return;
        }
        if (binary.operator() == Operator.SUBTRACT && binary.value().compareTo(Rational.ZERO) < 0) {
            throw new AssertionError("减法产生负数：" + binary.format());
        }
        if (binary.operator() == Operator.DIVIDE && !binary.value().isProperFraction()) {
            throw new AssertionError("除法结果不是真分数：" + binary.format());
        }
        validateNode(binary.left());
        validateNode(binary.right());
    }
}
