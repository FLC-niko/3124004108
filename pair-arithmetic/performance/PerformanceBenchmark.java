import java.util.List;

/** 重复生成一万道题，给 JFR 留出足够的采样时间。 */
public final class PerformanceBenchmark {
    private PerformanceBenchmark() {
    }

    public static void main(String[] args) {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 10_000;
        int range = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        int rounds = args.length > 2 ? Integer.parseInt(args[2]) : 40;

        for (int index = 0; index < 3; index++) {
            new ExpressionGenerator().generate(count, range);
        }

        long start = System.nanoTime();
        long checksum = 0;
        for (int index = 0; index < rounds; index++) {
            List<Expression> expressions = new ExpressionGenerator().generate(count, range);
            checksum += expressions.size();
            checksum += expressions.get(index % expressions.size()).canonicalKey().length();
        }
        long elapsed = System.nanoTime() - start;
        double totalSeconds = elapsed / 1_000_000_000.0;
        double averageMilliseconds = elapsed / 1_000_000.0 / rounds;

        System.out.printf("题目数量：%d，数值范围：%d，测量轮数：%d%n", count, range, rounds);
        System.out.printf("总耗时：%.3f 秒%n", totalSeconds);
        System.out.printf("平均每次生成：%.3f 毫秒%n", averageMilliseconds);
        System.out.printf("平均生成速度：%.0f 题/秒%n", count / (averageMilliseconds / 1000.0));
        System.out.println("校验值：" + checksum);
    }
}
