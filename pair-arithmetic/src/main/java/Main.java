import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Main {
    private static final Set<String> KNOWN_ARGUMENTS = Set.of("-n", "-r", "-e", "-a");

    private Main() {
    }

    public static void main(String[] args) {
        int status = run(args, System.out, System.err, Path.of(".").toAbsolutePath().normalize());
        if (status != 0) {
            System.exit(status);
        }
    }

    static int run(String[] args, PrintStream output, PrintStream error, Path workingDirectory) {
        try {
            Map<String, String> options = parseArguments(args);
            boolean gradingMode = options.containsKey("-e") || options.containsKey("-a");
            if (gradingMode) {
                runGrading(options, workingDirectory);
                output.println("批改完成，结果已写入 " + workingDirectory.resolve("Grade.txt"));
            } else {
                runGeneration(options, workingDirectory);
                output.println("题目和答案已写入 " + workingDirectory);
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException | IOException exception) {
            error.println("错误：" + exception.getMessage());
            printHelp(error);
            return 1;
        }
    }

    private static void runGeneration(Map<String, String> options, Path workingDirectory) throws IOException {
        if (!options.containsKey("-r")) {
            throw new IllegalArgumentException("生成题目时必须提供 -r 参数");
        }
        if (options.containsKey("-e") || options.containsKey("-a")) {
            throw new IllegalArgumentException("生成参数不能和批改参数混用");
        }
        int count = parsePositiveInteger(options.getOrDefault("-n", "10"), "-n");
        int range = parsePositiveInteger(options.get("-r"), "-r");
        List<Expression> expressions = new ExpressionGenerator().generate(count, range);
        ExerciseFiles.writeExercises(expressions, workingDirectory);
    }

    private static void runGrading(Map<String, String> options, Path workingDirectory) throws IOException {
        if (!options.containsKey("-e") || !options.containsKey("-a")) {
            throw new IllegalArgumentException("批改时必须同时提供 -e 和 -a 参数");
        }
        if (options.containsKey("-n") || options.containsKey("-r")) {
            throw new IllegalArgumentException("批改参数不能和生成参数混用");
        }
        new AnswerGrader().grade(
                Path.of(options.get("-e")),
                Path.of(options.get("-a")),
                workingDirectory.resolve("Grade.txt"));
    }

    private static Map<String, String> parseArguments(String[] args) {
        if (args.length == 0 || args.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须按“选项 值”成对给出");
        }
        Map<String, String> options = new HashMap<>();
        for (int index = 0; index < args.length; index += 2) {
            String option = args[index];
            if (!KNOWN_ARGUMENTS.contains(option)) {
                throw new IllegalArgumentException("未知参数：" + option);
            }
            if (options.put(option, args[index + 1]) != null) {
                throw new IllegalArgumentException("参数重复：" + option);
            }
        }
        return options;
    }

    private static int parsePositiveInteger(String text, String option) {
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(option + " 后面必须是正整数");
        }
    }

    private static void printHelp(PrintStream output) {
        output.println("生成：java -jar Myapp.jar [-n 题目数量] -r 数值范围");
        output.println("批改：java -jar Myapp.jar -e Exercises.txt -a Answers.txt");
    }
}
