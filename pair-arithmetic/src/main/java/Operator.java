public enum Operator {
    ADD("+", 1, true),
    SUBTRACT("−", 1, false),
    MULTIPLY("×", 2, true),
    DIVIDE("÷", 2, false);

    private final String symbol;
    private final int precedence;
    private final boolean commutative;

    Operator(String symbol, int precedence, boolean commutative) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.commutative = commutative;
    }

    public String symbol() {
        return symbol;
    }

    public int precedence() {
        return precedence;
    }

    public boolean commutative() {
        return commutative;
    }
}
