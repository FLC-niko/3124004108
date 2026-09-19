import java.math.BigInteger;
import java.util.Objects;

/** 不丢失精度的非浮点有理数。 */
public final class Rational implements Comparable<Rational> {
    public static final Rational ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE);
    public static final Rational ONE = new Rational(BigInteger.ONE, BigInteger.ONE);

    private final BigInteger numerator;
    private final BigInteger denominator;

    public Rational(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public Rational(BigInteger numerator, BigInteger denominator) {
        Objects.requireNonNull(numerator, "numerator");
        Objects.requireNonNull(denominator, "denominator");
        if (denominator.signum() == 0) {
            throw new ArithmeticException("分母不能为 0");
        }
        if (denominator.signum() < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
        BigInteger gcd = numerator.gcd(denominator);
        this.numerator = numerator.divide(gcd);
        this.denominator = denominator.divide(gcd);
    }

    public static Rational of(long value) {
        return new Rational(value, 1);
    }

    public Rational add(Rational other) {
        return new Rational(
                numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
                denominator.multiply(other.denominator));
    }

    public Rational subtract(Rational other) {
        return new Rational(
                numerator.multiply(other.denominator).subtract(other.numerator.multiply(denominator)),
                denominator.multiply(other.denominator));
    }

    public Rational multiply(Rational other) {
        return new Rational(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
    }

    public Rational divide(Rational other) {
        if (other.isZero()) {
            throw new ArithmeticException("除数不能为 0");
        }
        return new Rational(numerator.multiply(other.denominator), denominator.multiply(other.numerator));
    }

    public boolean isZero() {
        return numerator.signum() == 0;
    }

    public boolean isProperFraction() {
        return numerator.signum() > 0 && numerator.compareTo(denominator) < 0;
    }

    public String toDisplayString() {
        BigInteger[] parts = numerator.divideAndRemainder(denominator);
        if (parts[1].signum() == 0) {
            return parts[0].toString();
        }
        if (numerator.signum() < 0 && parts[0].signum() == 0) {
            return "-" + parts[1].abs() + "/" + denominator;
        }
        if (parts[0].signum() == 0) {
            return parts[1] + "/" + denominator;
        }
        return parts[0] + "’" + parts[1].abs() + "/" + denominator;
    }

    public static Rational parse(String text) {
        String value = text.trim().replace('\'', '’');
        int mixedMark = value.indexOf('’');
        if (mixedMark >= 0) {
            BigInteger whole = new BigInteger(value.substring(0, mixedMark));
            Rational fraction = parse(value.substring(mixedMark + 1));
            Rational magnitude = of(whole.abs().longValueExact()).add(fraction);
            return whole.signum() < 0 ? ZERO.subtract(magnitude) : magnitude;
        }
        int slash = value.indexOf('/');
        if (slash >= 0) {
            return new Rational(
                    new BigInteger(value.substring(0, slash)),
                    new BigInteger(value.substring(slash + 1)));
        }
        return new Rational(new BigInteger(value), BigInteger.ONE);
    }

    @Override
    public int compareTo(Rational other) {
        return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Rational rational)) {
            return false;
        }
        return numerator.equals(rational.numerator) && denominator.equals(rational.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }
}
