package JUnit;

/**
 * This class represents passed test and contains information about it.
 */
public class PassedTest {
    /**
     * Name of method.
     */
    private String name;
    /**
     * Running time of method.
     */
    private long time;

    public PassedTest(String name, long time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassedTest)) return false;

        PassedTest that = (PassedTest) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
