import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MainTest {
    public void generationCreatesRequiredFiles() throws Exception {
        Path directory = Files.createTempDirectory("arithmetic-main-");
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        int status = Main.run(
                new String[]{"-n", "12", "-r", "10"},
                System.out,
                new PrintStream(errors, true, StandardCharsets.UTF_8),
                directory);
        TestSupport.assertEquals(0, status);
        TestSupport.assertEquals(12, Files.readAllLines(directory.resolve("Exercises.txt")).size());
        TestSupport.assertEquals(12, Files.readAllLines(directory.resolve("Answers.txt")).size());
        TestSupport.assertEquals("", errors.toString(StandardCharsets.UTF_8));
    }

    public void rangeIsRequired() throws Exception {
        Path directory = Files.createTempDirectory("arithmetic-main-error-");
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        int status = Main.run(
                new String[]{"-n", "10"},
                System.out,
                new PrintStream(errors, true, StandardCharsets.UTF_8),
                directory);
        TestSupport.assertEquals(1, status);
        TestSupport.assertTrue(errors.toString(StandardCharsets.UTF_8).contains("必须提供 -r"),
                "应该提示缺少 -r");
    }

    public void generatedAnswersPassGrading() throws Exception {
        Path directory = Files.createTempDirectory("arithmetic-roundtrip-");
        int generationStatus = Main.run(
                new String[]{"-n", "50", "-r", "10"}, System.out, System.err, directory);
        TestSupport.assertEquals(0, generationStatus);
        int gradingStatus = Main.run(new String[]{
                "-e", directory.resolve("Exercises.txt").toString(),
                "-a", directory.resolve("Answers.txt").toString()
        }, System.out, System.err, directory);
        TestSupport.assertEquals(0, gradingStatus);
        TestSupport.assertEquals(List.of("Correct: 50 (" + joinedNumbers(50) + ")", "Wrong: 0 ()"),
                Files.readAllLines(directory.resolve("Grade.txt"), StandardCharsets.UTF_8));
    }

    private static String joinedNumbers(int count) {
        StringBuilder result = new StringBuilder();
        for (int number = 1; number <= count; number++) {
            if (number > 1) {
                result.append(", ");
            }
            result.append(number);
        }
        return result.toString();
    }
}
