package JUnit;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents ignored test and contains information about it.
 */
public class IgnoredTest {
    /**
     * Name of method.
     */
    private String name;
    /**
     * Message with reason for ignoring the test.
     */
    private String reason;

    public IgnoredTest(@NotNull String name, @NotNull  String reason) {
        this.name = name;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgnoredTest)) return false;

        IgnoredTest that = (IgnoredTest) o;

        if (!name.equals(that.name)) return false;
        return reason.equals(that.reason);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + reason.hashCode();
        return result;
    }
}
