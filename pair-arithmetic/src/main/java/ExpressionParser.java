/** 解析判题文件中的四则运算表达式。 */
public final class ExpressionParser {
    private final String input;
    private int position;

    private ExpressionParser(String input) {
        this.input = input;
    }

    public static Expression parse(String text) {
        ExpressionParser parser = new ExpressionParser(text);
        Expression expression = parser.parseAddSubtract();
        parser.skipSpaces();
        if (parser.position != parser.input.length()) {
            throw parser.error("存在无法识别的字符");
        }
        return expression;
    }

    private Expression parseAddSubtract() {
        Expression result = parseMultiplyDivide();
        while (true) {
            skipSpaces();
            if (consume('+')) {
                result = new BinaryExpression(result, Operator.ADD, parseMultiplyDivide());
            } else if (consume('-') || consume('−')) {
                result = new BinaryExpression(result, Operator.SUBTRACT, parseMultiplyDivide());
            } else {
                return result;
            }
        }
    }

    private Expression parseMultiplyDivide() {
        Expression result = parseFactor();
        while (true) {
            skipSpaces();
            if (consume('*') || consume('×')) {
                result = new BinaryExpression(result, Operator.MULTIPLY, parseFactor());
            } else if (consume('/') || consume('÷')) {
                result = new BinaryExpression(result, Operator.DIVIDE, parseFactor());
            } else {
                return result;
            }
        }
    }

    private Expression parseFactor() {
        skipSpaces();
        if (consume('(')) {
            Expression expression = parseAddSubtract();
            skipSpaces();
            if (!consume(')')) {
                throw error("缺少右括号");
            }
            return expression;
        }

        int start = position;
        while (position < input.length()) {
            char current = input.charAt(position);
            if (Character.isDigit(current) || current == '/' || current == '\'' || current == '’') {
                position++;
            } else {
                break;
            }
        }
        if (start == position) {
            throw error("这里应该是一个数字");
        }
        try {
            return new NumberExpression(Rational.parse(input.substring(start, position)));
        } catch (RuntimeException exception) {
            throw error("分数格式不正确");
        }
    }

    private boolean consume(char expected) {
        if (position < input.length() && input.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipSpaces() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + "（位置 " + position + "）");
    }
}
