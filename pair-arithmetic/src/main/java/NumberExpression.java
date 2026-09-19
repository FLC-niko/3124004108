import java.util.Objects;

public final class NumberExpression implements Expression {
    private final Rational value;

    public NumberExpression(Rational value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    @Override
    public Rational value() {
        return value;
    }

    @Override
    public int operatorCount() {
        return 0;
    }

    @Override
    public int precedence() {
        return 3;
    }

    @Override
    public String canonicalKey() {
        return "N(" + value + ")";
    }

    @Override
    public String format() {
        return value.toDisplayString();
    }
}
