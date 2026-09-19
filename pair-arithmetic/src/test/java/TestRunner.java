import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        List<Class<?>> testClasses = List.of(CoreModelTest.class);
        int passed = 0;
        List<String> failures = new ArrayList<>();

        for (Class<?> testClass : testClasses) {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            Method[] methods = testClass.getDeclaredMethods();
            List<Method> tests = java.util.Arrays.stream(methods)
                    .filter(method -> Modifier.isPublic(method.getModifiers()))
                    .filter(method -> !Modifier.isStatic(method.getModifiers()))
                    .filter(method -> method.getParameterCount() == 0)
                    .filter(method -> !method.isSynthetic())
                    .sorted(Comparator.comparing(Method::getName))
                    .toList();
            for (Method test : tests) {
                try {
                    test.invoke(instance);
                    passed++;
                    System.out.println("[通过] " + testClass.getSimpleName() + "." + test.getName());
                } catch (InvocationTargetException exception) {
                    Throwable cause = exception.getCause();
                    failures.add(testClass.getSimpleName() + "." + test.getName() + ": " + cause);
                    System.out.println("[失败] " + failures.get(failures.size() - 1));
                }
            }
        }

        System.out.println("共 " + (passed + failures.size()) + " 项，成功 " + passed
                + " 项，失败 " + failures.size() + " 项");
        if (!failures.isEmpty()) {
            throw new AssertionError("存在测试失败");
        }
    }
}
