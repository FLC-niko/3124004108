import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public final class ExpressionGeneratorTest {
    public void generatedExpressionsAreUnique() {
        List<Expression> expressions = generator().generate(500, 10);
        Set<ExpressionKey> keys = new HashSet<>();
        for (Expression expression : expressions) {
            TestSupport.assertTrue(keys.add(expression.equivalenceKey()), "发现重复表达式");
        }
    }

    public void operatorCountNeverExceedsThree() {
        for (Expression expression : generator().generate(300, 10)) {
            TestSupport.assertTrue(expression.operatorCount() <= 3, "运算符超过 3 个");
            TestSupport.assertTrue(expression.operatorCount() >= 1, "题目应该包含运算符");
        }
    }

    public void subtractionNeverProducesNegativeValue() {
        for (Expression expression : generator().generate(300, 10)) {
            assertSubtractions(expression);
        }
    }

    public void everyDivisionProducesProperFraction() {
        for (Expression expression : generator().generate(300, 10)) {
            assertDivisions(expression);
        }
    }

    public void rangeOneIsAccepted() {
        List<Expression> expressions = generator().generate(10, 1);
        TestSupport.assertEquals(10, expressions.size());
    }

    private static ExpressionGenerator generator() {
        return new ExpressionGenerator(new SplittableRandom(3124004108L));
    }

    private static void assertSubtractions(Expression expression) {
        if (expression instanceof BinaryExpression binary) {
            if (binary.operator() == Operator.SUBTRACT) {
                TestSupport.assertTrue(binary.value().compareTo(Rational.ZERO) >= 0,
                        "减法产生了负数：" + binary.format());
            }
            assertSubtractions(binary.left());
            assertSubtractions(binary.right());
        }
    }

    private static void assertDivisions(Expression expression) {
        if (expression instanceof BinaryExpression binary) {
            if (binary.operator() == Operator.DIVIDE) {
                TestSupport.assertTrue(binary.value().isProperFraction(),
                        "除法没有产生真分数：" + binary.format());
            }
            assertDivisions(binary.left());
            assertDivisions(binary.right());
        }
    }
}
