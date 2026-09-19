public final class CoreModelTest {
    public void fractionIsReduced() {
        TestSupport.assertEquals("1/2", new Rational(4, 8).toDisplayString());
    }

    public void mixedFractionIsFormatted() {
        TestSupport.assertEquals("2’3/8", new Rational(19, 8).toDisplayString());
    }

    public void mixedFractionCanBeParsed() {
        TestSupport.assertEquals(new Rational(19, 8), Rational.parse("2'3/8"));
    }

    public void rationalArithmeticIsExact() {
        Rational result = new Rational(1, 6).add(new Rational(1, 8));
        TestSupport.assertEquals(new Rational(7, 24), result);
    }

    public void commutativeChildrenShareKey() {
        Expression first = binary(number(2), Operator.ADD, number(3));
        Expression second = binary(number(3), Operator.ADD, number(2));
        TestSupport.assertEquals(first.canonicalKey(), second.canonicalKey());
    }

    public void nonCommutativeChildrenKeepOrder() {
        Expression first = binary(number(3), Operator.SUBTRACT, number(2));
        Expression second = binary(number(2), Operator.SUBTRACT, number(3));
        TestSupport.assertTrue(!first.canonicalKey().equals(second.canonicalKey()), "减法不应该交换左右两边");
    }

    public void nestedCommutativeExampleIsDetected() {
        Expression first = binary(number(3), Operator.ADD,
                binary(number(2), Operator.ADD, number(1)));
        Expression second = binary(
                binary(number(1), Operator.ADD, number(2)), Operator.ADD, number(3));
        TestSupport.assertEquals(first.canonicalKey(), second.canonicalKey());
    }

    public void differentLeftAssociativeTreeIsKept() {
        Expression first = binary(binary(number(1), Operator.ADD, number(2)), Operator.ADD, number(3));
        Expression second = binary(binary(number(3), Operator.ADD, number(2)), Operator.ADD, number(1));
        TestSupport.assertTrue(!first.canonicalKey().equals(second.canonicalKey()), "题目要求这两棵树不视为重复");
    }

    public void parenthesesPreserveDivisionRightChild() {
        Expression expression = binary(number(1), Operator.DIVIDE,
                binary(number(2), Operator.MULTIPLY, number(3)));
        TestSupport.assertEquals("1 ÷ (2 × 3)", expression.format());
    }

    private static NumberExpression number(long value) {
        return new NumberExpression(Rational.of(value));
    }

    private static BinaryExpression binary(Expression left, Operator operator, Expression right) {
        return new BinaryExpression(left, operator, right);
    }
}
