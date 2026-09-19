public interface Expression {
    Rational value();

    int operatorCount();

    int precedence();

    ExpressionKey equivalenceKey();

    String canonicalKey();

    String format();
}
