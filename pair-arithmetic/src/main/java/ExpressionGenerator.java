import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

/** 生成满足约束且不重复的表达式。 */
public final class ExpressionGenerator {
    private static final Operator[] OPERATORS = Operator.values();

    private final SplittableRandom random;

    public ExpressionGenerator() {
        this(new SplittableRandom());
    }

    ExpressionGenerator(SplittableRandom random) {
        this.random = random;
    }

    public List<Expression> generate(int count, int range) {
        if (count <= 0) {
            throw new IllegalArgumentException("题目数量必须是正整数");
        }
        if (range <= 0) {
            throw new IllegalArgumentException("数值范围必须是正整数");
        }

        List<Expression> result = new ArrayList<>(count);
        Set<ExpressionKey> keys = new HashSet<>(Math.max(16, count * 2));
        long maximumAttempts = Math.max(20_000L, count * 2_000L);

        for (long attempts = 0; result.size() < count && attempts < maximumAttempts; attempts++) {
            int operatorCount = 1 + random.nextInt(3);
            Expression candidate = buildExpression(operatorCount, range);
            if (candidate != null && keys.add(candidate.equivalenceKey())) {
                result.add(candidate);
            }
        }

        if (result.size() != count) {
            throw new IllegalStateException("在当前数值范围内无法生成足够多的不重复题目，请增大 -r");
        }
        return result;
    }

    private Expression buildExpression(int operatorCount, int range) {
        if (operatorCount == 0) {
            return randomNumber(range);
        }

        int leftOperatorCount = random.nextInt(operatorCount);
        int rightOperatorCount = operatorCount - 1 - leftOperatorCount;
        Expression left = buildExpression(leftOperatorCount, range);
        Expression right = buildExpression(rightOperatorCount, range);

        int start = random.nextInt(OPERATORS.length);
        for (int offset = 0; offset < OPERATORS.length; offset++) {
            Operator operator = OPERATORS[(start + offset) % OPERATORS.length];
            BinaryExpression expression = createIfValid(left, operator, right);
            if (expression != null) {
                return expression;
            }
        }
        return null;
    }

    private BinaryExpression createIfValid(Expression left, Operator operator, Expression right) {
        if (operator == Operator.SUBTRACT && left.value().compareTo(right.value()) < 0) {
            Expression temporary = left;
            left = right;
            right = temporary;
        }

        if (operator == Operator.DIVIDE) {
            if (left.value().isZero() || right.value().isZero()
                    || left.value().compareTo(right.value()) == 0) {
                return null;
            }
            if (left.value().compareTo(right.value()) > 0) {
                Expression temporary = left;
                left = right;
                right = temporary;
            }
            BinaryExpression division = new BinaryExpression(left, operator, right);
            return division.value().isProperFraction() ? division : null;
        }

        return new BinaryExpression(left, operator, right);
    }

    private NumberExpression randomNumber(int range) {
        if (range >= 3 && random.nextInt(100) < 45) {
            int denominator = 2 + random.nextInt(range - 2);
            int numerator = 1 + random.nextInt(denominator - 1);
            int whole = random.nextInt(range);
            Rational fraction = new Rational(numerator, denominator);
            return new NumberExpression(Rational.of(whole).add(fraction));
        }
        return new NumberExpression(Rational.of(random.nextInt(range)));
    }
}
