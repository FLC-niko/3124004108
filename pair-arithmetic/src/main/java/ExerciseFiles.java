import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExerciseFiles {
    private ExerciseFiles() {
    }

    public static void writeExercises(List<Expression> expressions, Path directory) throws IOException {
        List<String> exercises = new ArrayList<>(expressions.size());
        List<String> answers = new ArrayList<>(expressions.size());
        for (int index = 0; index < expressions.size(); index++) {
            Expression expression = expressions.get(index);
            int number = index + 1;
            exercises.add(number + ". " + expression.format() + " =");
            answers.add(number + ". " + expression.value().toDisplayString());
        }
        Files.write(directory.resolve("Exercises.txt"), exercises, StandardCharsets.UTF_8);
        Files.write(directory.resolve("Answers.txt"), answers, StandardCharsets.UTF_8);
    }

    public static Map<Integer, String> readNumberedLines(Path path) throws IOException {
        Map<Integer, String> entries = new LinkedHashMap<>();
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            int dot = line.indexOf('.');
            if (dot <= 0) {
                throw new IllegalArgumentException("文件行缺少“编号.”：" + line);
            }
            int number;
            try {
                number = Integer.parseInt(line.substring(0, dot).trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("题号不是整数：" + line, exception);
            }
            if (entries.put(number, line.substring(dot + 1).trim()) != null) {
                throw new IllegalArgumentException("题号重复：" + number);
            }
        }
        return entries;
    }
}
