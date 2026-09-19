public interface Expression {
    Rational value();

    int operatorCount();

    int precedence();

    String canonicalKey();

    String format();
}
