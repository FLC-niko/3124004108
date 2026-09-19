import java.util.Objects;

public final class BinaryExpression implements Expression {
    private final Expression left;
    private final Operator operator;
    private final Expression right;
    private final Rational value;

    public BinaryExpression(Expression left, Operator operator, Expression right) {
        this.left = Objects.requireNonNull(left, "left");
        this.operator = Objects.requireNonNull(operator, "operator");
        this.right = Objects.requireNonNull(right, "right");
        this.value = calculate(left.value(), operator, right.value());
    }

    private static Rational calculate(Rational left, Operator operator, Rational right) {
        return switch (operator) {
            case ADD -> left.add(right);
            case SUBTRACT -> left.subtract(right);
            case MULTIPLY -> left.multiply(right);
            case DIVIDE -> left.divide(right);
        };
    }

    public Expression left() {
        return left;
    }

    public Expression right() {
        return right;
    }

    public Operator operator() {
        return operator;
    }

    @Override
    public Rational value() {
        return value;
    }

    @Override
    public int operatorCount() {
        return 1 + left.operatorCount() + right.operatorCount();
    }

    @Override
    public int precedence() {
        return operator.precedence();
    }

    @Override
    public String canonicalKey() {
        String leftKey = left.canonicalKey();
        String rightKey = right.canonicalKey();
        if (operator.commutative() && leftKey.compareTo(rightKey) > 0) {
            String temporary = leftKey;
            leftKey = rightKey;
            rightKey = temporary;
        }
        return operator.name() + "(" + leftKey + "," + rightKey + ")";
    }

    @Override
    public String format() {
        return formatChild(left, false) + " " + operator.symbol() + " " + formatChild(right, true);
    }

    private String formatChild(Expression child, boolean rightChild) {
        if (!(child instanceof BinaryExpression childBinary)) {
            return child.format();
        }
        boolean parentheses = child.precedence() < precedence();
        if (rightChild && child.precedence() == precedence()) {
            parentheses = switch (operator) {
                case ADD -> childBinary.operator == Operator.SUBTRACT;
                case SUBTRACT, DIVIDE -> true;
                case MULTIPLY -> childBinary.operator == Operator.DIVIDE;
            };
        }
        String text = child.format();
        return parentheses ? "(" + text + ")" : text;
    }
}
