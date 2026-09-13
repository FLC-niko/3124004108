import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Unit tests for tokenization, calculation, file handling, and CLI validation. */
final class SimilarityCalculatorTest {
    private final SimilarityCalculator calculator = new SimilarityCalculator();

    void identicalEnglishTextIsOne() {
        SimilarityResult result = calculator.calculate("Software engineering matters.",
                "software ENGINEERING matters");
        TestSupport.assertEquals(1.0, result.getSimilarity(), 0.000001,
                "大小写和标点不应影响完全相同文本");
    }

    void unrelatedTextIsZero() {
        SimilarityResult result = calculator.calculate("apple orange", "database compiler");
        TestSupport.assertEquals(0.0, result.getSimilarity(), 0.000001,
                "没有共同词语时相似度应为零");
    }

    void twoEmptyTextsAreEqual() {
        SimilarityResult result = calculator.calculate("   ，。", "\n\t");
        TestSupport.assertEquals(1.0, result.getSimilarity(), 0.000001,
                "两个没有有效 token 的文本应视为相同");
    }

    void oneEmptyTextIsDifferent() {
        SimilarityResult result = calculator.calculate("", "one word");
        TestSupport.assertEquals(0.0, result.getSimilarity(), 0.000001,
                "只有一侧为空时相似度应为零");
    }

    void addedContentLowersSimilarityButKeepsOverlap() {
        SimilarityResult result = calculator.calculate("alpha beta", "alpha beta gamma delta");
        TestSupport.assertTrue(result.getSimilarity() > 0.0 && result.getSimilarity() < 1.0,
                "增加无关内容后相似度应处于开区间");
    }

    void deletedContentLowersSimilarityButKeepsOverlap() {
        SimilarityResult result = calculator.calculate("alpha beta gamma", "alpha beta");
        TestSupport.assertTrue(result.getSimilarity() > 0.0 && result.getSimilarity() < 1.0,
                "删除部分内容后相似度应处于开区间");
    }

    void replacementWithNoOverlapIsZero() {
        SimilarityResult result = calculator.calculate("red green blue", "black white yellow");
        TestSupport.assertEquals(0.0, result.getSimilarity(), 0.000001,
                "全部替换且没有共同词语时相似度应为零");
    }

    void repeatedWordsAffectTermFrequency() {
        SimilarityResult result = calculator.calculate("cat cat dog", "cat dog dog");
        TestSupport.assertTrue(result.getSimilarity() > 0.0 && result.getSimilarity() < 1.0,
                "重复词应参与词频计算");
    }

    void ChineseTextCanBeComparedWithoutDictionary() {
        SimilarityResult result = calculator.calculate(
                "今天是星期天，天气晴，今天晚上我要去看电影。",
                "今天是周天，天气晴朗，我晚上要去看电影。");
        TestSupport.assertTrue(result.getSimilarity() > 0.5 && result.getSimilarity() < 1.0,
                "中文增删改文本应保留明显的相似度");
    }

    void punctuationAndWhitespaceAreIgnored() {
        SimilarityResult result = calculator.calculate("Hello, world!", "hello world");
        TestSupport.assertEquals(1.0, result.getSimilarity(), 0.000001,
                "标点和空白不应成为相似度差异");
    }

    void mixedLanguageTextIsSupported() {
        SimilarityResult result = calculator.calculate("Java 17 可以运行中文程序", "java17可以运行中文程序");
        TestSupport.assertTrue(result.getSimilarity() > 0.5,
                "中英文混合文本应能被正常分词");
    }

    void tokenizerReportsTokenCount() {
        SimilarityResult result = calculator.calculate("one two two", "one");
        TestSupport.assertEquals(3, result.getOriginalTokenCount(), "原文 token 数量不正确");
        TestSupport.assertEquals(1, result.getPlagiarizedTokenCount(), "抄袭版 token 数量不正确");
    }

    void commandLineWritesTwoDecimalAnswer() throws Exception {
        Path directory = Files.createTempDirectory("plagiarism-cli-test-");
        Path original = directory.resolve("original.txt");
        Path plagiarized = directory.resolve("plagiarized.txt");
        Path answer = directory.resolve("answer.txt");
        Files.writeString(original, "same text", StandardCharsets.UTF_8);
        Files.writeString(plagiarized, "same text", StandardCharsets.UTF_8);

        Main.execute(new String[] {original.toString(), plagiarized.toString(), answer.toString()});

        TestSupport.assertEquals("1.00\n", Files.readString(answer, StandardCharsets.UTF_8),
                "答案文件应只包含两位小数和换行");
        deleteDirectory(directory);
    }

    void wrongArgumentCountIsRejected() {
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> Main.execute(new String[] {"only-one-path"}),
                "参数数量错误应被拒绝");
    }

    void relativePathIsRejected() {
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> Main.execute(new String[] {"a.txt", "/tmp/b.txt", "/tmp/c.txt"}),
                "相对路径应被拒绝");
    }

    void missingInputFileIsReported() throws IOException {
        Path directory = Files.createTempDirectory("plagiarism-missing-test-");
        Path missing = directory.resolve("missing.txt");
        Path output = directory.resolve("answer.txt");

        TestSupport.assertThrows(IOException.class,
                () -> Main.execute(new String[] {missing.toString(), missing.toString(), output.toString()}),
                "输入文件不存在应报告 IOException");
        deleteDirectory(directory);
    }

    private static void deleteDirectory(Path directory) throws IOException {
        try (var paths = Files.walk(directory)) {
            paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}
