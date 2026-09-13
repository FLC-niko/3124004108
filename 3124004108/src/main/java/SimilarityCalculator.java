import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Calculates TF cosine similarity between two pieces of text. */
public final class SimilarityCalculator {
    private final TextTokenizer tokenizer;

    public SimilarityCalculator() {
        this(new TextTokenizer());
    }

    public SimilarityCalculator(TextTokenizer tokenizer) {
        this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
    }

    public SimilarityResult calculate(String original, String plagiarized) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(plagiarized, "plagiarized");

        Map<String, Integer> originalFrequency = frequency(tokenizer.tokenize(original));
        Map<String, Integer> plagiarizedFrequency = frequency(tokenizer.tokenize(plagiarized));
        double similarity = cosineSimilarity(originalFrequency, plagiarizedFrequency);

        return new SimilarityResult(
                similarity,
                originalFrequency.values().stream().mapToInt(Integer::intValue).sum(),
                plagiarizedFrequency.values().stream().mapToInt(Integer::intValue).sum());
    }

    private static Map<String, Integer> frequency(List<String> tokens) {
        Map<String, Integer> result = new HashMap<>();
        for (String token : tokens) {
            result.merge(token, 1, Integer::sum);
        }
        return result;
    }

    private static double cosineSimilarity(Map<String, Integer> left,
            Map<String, Integer> right) {
        if (left.isEmpty() && right.isEmpty()) {
            return 1.0;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        for (Map.Entry<String, Integer> entry : left.entrySet()) {
            dotProduct += (double) entry.getValue() * right.getOrDefault(entry.getKey(), 0);
        }

        double leftLength = vectorLength(left);
        double rightLength = vectorLength(right);
        if (dotProduct == 0.0) {
            return 0.0;
        }

        double value = dotProduct / (leftLength * rightLength);
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double vectorLength(Map<String, Integer> vector) {
        double sum = 0.0;
        for (int frequency : vector.values()) {
            sum += (double) frequency * frequency;
        }
        return Math.sqrt(sum);
    }
}
