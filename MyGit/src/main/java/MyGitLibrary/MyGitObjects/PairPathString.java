package MyGitLibrary.MyGitObjects;

import java.nio.file.Path;

/**
 * Auxiliary class that stores pairs of path and hash.
 */
class PairPathString {

    private Path path;
    private String string;

    PairPathString(Path path, String string) {
        this.path = path;
        this.string = string;
    }

    Path getPath() {
        return path;
    }

    String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PairPathString that = (PairPathString) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return string != null ? string.equals(that.string) : that.string == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (string != null ? string.hashCode() : 0);
        return result;
    }
}
