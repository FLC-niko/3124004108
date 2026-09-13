/** Immutable result returned by the similarity calculator. */
public final class SimilarityResult {
    private final double similarity;
    private final int originalTokenCount;
    private final int plagiarizedTokenCount;

    public SimilarityResult(double similarity, int originalTokenCount,
            int plagiarizedTokenCount) {
        this.similarity = similarity;
        this.originalTokenCount = originalTokenCount;
        this.plagiarizedTokenCount = plagiarizedTokenCount;
    }

    public double getSimilarity() {
        return similarity;
    }

    public int getOriginalTokenCount() {
        return originalTokenCount;
    }

    public int getPlagiarizedTokenCount() {
        return plagiarizedTokenCount;
    }
}
