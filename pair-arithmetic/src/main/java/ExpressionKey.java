import java.util.Objects;

/** 用对象结构表示表达式等价关系，避免批量生成时反复拼接长字符串。 */
public interface ExpressionKey {
    static ExpressionKey number(Rational value) {
        return new NumberKey(value);
    }

    static ExpressionKey binary(Operator operator, ExpressionKey left, ExpressionKey right) {
        return new BinaryKey(operator, left, right);
    }

    record NumberKey(Rational value) implements ExpressionKey {
        public NumberKey {
            Objects.requireNonNull(value, "value");
        }
    }

    final class BinaryKey implements ExpressionKey {
        private final Operator operator;
        private final ExpressionKey left;
        private final ExpressionKey right;

        private BinaryKey(Operator operator, ExpressionKey left, ExpressionKey right) {
            this.operator = Objects.requireNonNull(operator, "operator");
            this.left = Objects.requireNonNull(left, "left");
            this.right = Objects.requireNonNull(right, "right");
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BinaryKey key) || operator != key.operator) {
                return false;
            }
            boolean sameOrder = left.equals(key.left) && right.equals(key.right);
            if (!operator.commutative()) {
                return sameOrder;
            }
            return sameOrder || left.equals(key.right) && right.equals(key.left);
        }

        @Override
        public int hashCode() {
            if (operator.commutative()) {
                return 31 * operator.hashCode() + left.hashCode() + right.hashCode();
            }
            return Objects.hash(operator, left, right);
        }
    }
}
