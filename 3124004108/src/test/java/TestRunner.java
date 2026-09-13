import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/** Minimal dependency-free test runner used by scripts/test.sh. */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        int passed = 0;
        int failed = 0;
        SimilarityCalculatorTest testObject = new SimilarityCalculatorTest();

        Method[] methods = SimilarityCalculatorTest.class.getDeclaredMethods();
        Arrays.sort(methods, (left, right) -> left.getName().compareTo(right.getName()));
        for (Method method : methods) {
            if (!isTestMethod(method)) {
                continue;
            }
            try {
                method.invoke(testObject);
                passed++;
                System.out.println("PASS " + method.getName());
            } catch (InvocationTargetException exception) {
                failed++;
                Throwable cause = exception.getCause();
                System.err.println("FAIL " + method.getName() + ": " + cause.getMessage());
            }
        }

        System.out.printf("测试结果: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            throw new AssertionError("存在失败的测试用例");
        }
    }

    private static boolean isTestMethod(Method method) {
        return method.getName().matches("[a-z].*")
                && method.getParameterCount() == 0
                && !Modifier.isStatic(method.getModifiers())
                && !method.isSynthetic();
    }
}
