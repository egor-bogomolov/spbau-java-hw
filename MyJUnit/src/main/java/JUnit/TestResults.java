package JUnit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents results of running tests from some class.
 */
public class TestResults {
    /**
     * Lists with tests divided by result types.
     */
    private List<PassedTest> passed = new ArrayList<>();
    private List<FailedTest> failed = new ArrayList<>();
    private List<IgnoredTest> ignored = new ArrayList<>();

    public void addPassed(@NotNull PassedTest test) {
        passed.add(test);
    }

    public void addFailed(@NotNull FailedTest test) {
        failed.add(test);
    }

    public void addIgnored(@NotNull IgnoredTest test) {
        ignored.add(test);
    }

    public List<PassedTest> getPassed() {
        return passed;
    }

    public List<FailedTest> getFailed() {
        return failed;
    }

    public List<IgnoredTest> getIgnored() {
        return ignored;
    }
}
