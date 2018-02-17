package JUnit;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents failed test and contains information about it.
 */
public class FailedTest {
    /**
     * Name of method.
     */
    private String name;
    /**
     * Exception that was thrown and caused fail. Set to null if exception was expected but wasn't thrown.
     */
    private Throwable throwable;

    public FailedTest(@NotNull String name, Throwable throwable) {
        this.name = name;
        this.throwable = throwable;
    }

    public String getName() {
        return name;
    }

    public Throwable getException() {
        return throwable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailedTest)) return false;

        FailedTest that = (FailedTest) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
