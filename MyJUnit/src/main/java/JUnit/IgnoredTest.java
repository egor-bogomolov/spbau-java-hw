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
}
