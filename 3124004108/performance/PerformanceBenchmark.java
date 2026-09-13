/**
 * Small repeatable benchmark used when collecting a profiler screenshot.
 * It does not belong to the submitted command-line program.
 */
public final class PerformanceBenchmark {
    private PerformanceBenchmark() {
    }

    public static void main(String[] args) {
        int iterations = parseIterations(args);
        String original = ("软件工程需要清晰的需求、可维护的代码和有效的测试。 ").repeat(2_000);
        String plagiarized = ("软件工程需要清晰的需求、可维护的代码和有效的测试，同时要记录复盘结果。 ")
                .repeat(2_000);
        SimilarityCalculator calculator = new SimilarityCalculator();

        long start = System.nanoTime();
        double checksum = 0.0;
        for (int i = 0; i < iterations; i++) {
            checksum += calculator.calculate(original, plagiarized).getSimilarity();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("iterations=%d elapsed_ms=%.2f checksum=%.6f%n",
                iterations, elapsed / 1_000_000.0, checksum);
    }

    private static int parseIterations(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("最多只能提供一个迭代次数参数");
        }
        if (args.length == 0) {
            return 30;
        }

        int iterations = Integer.parseInt(args[0]);
        if (iterations <= 0) {
            throw new IllegalArgumentException("迭代次数必须大于 0");
        }
        return iterations;
    }
}
