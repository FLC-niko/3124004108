import java.util.Objects;

/** Small assertion helpers so the test suite can run without a network dependency. */
final class TestSupport {
    private TestSupport() {
    }

    static void assertEquals(double expected, double actual, double tolerance, String message) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + "，期望 " + expected + "，实际 " + actual);
        }
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + "，期望 " + expected + "，实际 " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertThrows(Class<? extends Throwable> expectedType,
            ThrowingAction action, String message) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError(message + "，实际异常类型为 "
                    + actual.getClass().getName(), actual);
        }
        throw new AssertionError(message + "，没有抛出异常");
    }

    @FunctionalInterface
    interface ThrowingAction {
        void run() throws Exception;
    }
}
