import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ParserAndGraderTest {
    public void parserRespectsPrecedenceAndMixedFractions() {
        Rational result = ExpressionParser.parse("1’1/2 + 2 × 3").value();
        TestSupport.assertEquals(new Rational(15, 2), result);
    }

    public void parserRespectsParentheses() {
        Rational result = ExpressionParser.parse("1 ÷ (2 × 3)").value();
        TestSupport.assertEquals(new Rational(1, 6), result);
    }

    public void graderSeparatesCorrectAndWrongAnswers() throws Exception {
        Path directory = Files.createTempDirectory("arithmetic-grader-");
        Path exercises = directory.resolve("Exercises.txt");
        Path answers = directory.resolve("Answers.txt");
        Path grade = directory.resolve("Grade.txt");
        Files.write(exercises, List.of(
                "1. 1/6 + 1/8 =",
                "2. 3 − 1 =",
                "3. 1 ÷ (2 × 3) ="), StandardCharsets.UTF_8);
        Files.write(answers, List.of(
                "1. 7/24",
                "2. 3",
                "3. not-a-number"), StandardCharsets.UTF_8);

        GradeResult result = new AnswerGrader().grade(exercises, answers, grade);

        TestSupport.assertEquals(List.of(1), result.correct());
        TestSupport.assertEquals(List.of(2, 3), result.wrong());
        TestSupport.assertEquals(List.of("Correct: 1 (1)", "Wrong: 2 (2, 3)"),
                Files.readAllLines(grade, StandardCharsets.UTF_8));
    }

    public void missingAnswerIsWrong() throws Exception {
        Path directory = Files.createTempDirectory("arithmetic-missing-answer-");
        Path exercises = directory.resolve("Exercises.txt");
        Path answers = directory.resolve("Answers.txt");
        Files.writeString(exercises, "1. 1 + 1 =\n", StandardCharsets.UTF_8);
        Files.writeString(answers, "", StandardCharsets.UTF_8);
        GradeResult result = new AnswerGrader().grade(exercises, answers, directory.resolve("Grade.txt"));
        TestSupport.assertEquals(List.of(1), result.wrong());
    }
}
