package JUnit;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * This class represents test method and contains all needed information.
 */
public class TestMethod {
    /**
     * Class of exception that is expected to be thrown. If set to null then test shouldn't throw exceptions.
     */
    private Class expected = null;
    private Method method;

    public TestMethod(@NotNull Method method) {
        this.method = method;
    }

    public TestMethod(@NotNull Method method, @NotNull Class expected) {
        this.method = method;
        this.expected = expected;
    }

    public Class getExpected() {
        return expected;
    }

    public Method getMethod() {
        return method;
    }
}
