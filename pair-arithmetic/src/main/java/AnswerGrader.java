import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AnswerGrader {
    public GradeResult grade(Path exerciseFile, Path answerFile, Path outputFile) throws IOException {
        Map<Integer, String> exercises = ExerciseFiles.readNumberedLines(exerciseFile);
        Map<Integer, String> answers = ExerciseFiles.readNumberedLines(answerFile);
        List<Integer> correct = new ArrayList<>();
        List<Integer> wrong = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : exercises.entrySet()) {
            int number = entry.getKey();
            Rational expected = evaluateExercise(entry.getValue());
            String submitted = answers.get(number);
            if (submitted != null && answerEquals(expected, submitted)) {
                correct.add(number);
            } else {
                wrong.add(number);
            }
        }

        List<String> report = List.of(formatLine("Correct", correct), formatLine("Wrong", wrong));
        Files.write(outputFile, report, StandardCharsets.UTF_8);
        return new GradeResult(List.copyOf(correct), List.copyOf(wrong));
    }

    private static Rational evaluateExercise(String text) {
        String expression = text.trim();
        if (expression.endsWith("=")) {
            expression = expression.substring(0, expression.length() - 1).trim();
        }
        return ExpressionParser.parse(expression).value();
    }

    private static boolean answerEquals(Rational expected, String text) {
        try {
            return expected.equals(Rational.parse(text));
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static String formatLine(String label, List<Integer> numbers) {
        String joined = numbers.stream().map(String::valueOf).collect(Collectors.joining(", "));
        return label + ": " + numbers.size() + " (" + joined + ")";
    }
}
