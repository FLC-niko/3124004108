/**
 * Small repeatable benchmark used when collecting a profiler screenshot.
 * It does not belong to the submitted command-line program.
 */
public final class PerformanceBenchmark {
    private PerformanceBenchmark() {
    }

    public static void main(String[] args) {
        String original = ("软件工程需要清晰的需求、可维护的代码和有效的测试。 ").repeat(2_000);
        String plagiarized = ("软件工程需要清晰的需求、可维护的代码和有效的测试，同时要记录复盘结果。 ")
                .repeat(2_000);
        SimilarityCalculator calculator = new SimilarityCalculator();

        long start = System.nanoTime();
        double checksum = 0.0;
        for (int i = 0; i < 30; i++) {
            checksum += calculator.calculate(original, plagiarized).getSimilarity();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("iterations=30 elapsed_ms=%.2f checksum=%.6f%n",
                elapsed / 1_000_000.0, checksum);
    }
}
