import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/** Command-line entry point for the plagiarism checker. */
public final class Main {
    private static final String USAGE =
            "用法: java -jar main.jar <原文绝对路径> <抄袭版绝对路径> <答案绝对路径>";

    private Main() {
    }

    public static void main(String[] args) {
        try {
            execute(args);
        } catch (IllegalArgumentException | IOException exception) {
            System.err.println("程序执行失败: " + exception.getMessage());
            System.err.println(USAGE);
            System.exit(1);
        }
    }

    static void execute(String[] args) throws IOException {
        if (args == null || args.length != 3) {
            throw new IllegalArgumentException("需要提供原文、抄袭版和答案三个路径参数");
        }

        Path originalPath = Paths.get(args[0]);
        Path plagiarizedPath = Paths.get(args[1]);
        Path answerPath = Paths.get(args[2]);

        String original = FileService.readUtf8(originalPath);
        String plagiarized = FileService.readUtf8(plagiarizedPath);
        SimilarityResult result = new SimilarityCalculator().calculate(original, plagiarized);
        String answer = String.format(Locale.ROOT, "%.2f%n", result.getSimilarity());
        FileService.writeUtf8(answerPath, answer);
    }
}
