package MyGitLibrary.MyGitObjects;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains paths to all files contained in repository separated by files' types.
 */
public class StatusObject {
    private List<Path> staged = new ArrayList<>();
    private List<Path> unmodified = new ArrayList<>();
    private List<Path> modified = new ArrayList<>();
    private List<Path> deleted = new ArrayList<>();
    private List<Path> unversioned = new ArrayList<>();

    void addStaged(@NotNull Path path) {
        staged.add(path);
    }

    void addUnmodified(@NotNull Path path) {
        unmodified.add(path);
    }

    void addModified(@NotNull Path path) {
        modified.add(path);
    }

    void addDeleted(@NotNull Path path) {
        deleted.add(path);
    }

    void addUnversioned(@NotNull Path path) {
        unversioned.add(path);
    }

    public List<Path> getStaged() {
        return staged;
    }

    public List<Path> getUnmodified() {
        return unmodified;
    }

    public List<Path> getModified() {
        return modified;
    }

    public List<Path> getDeleted() {
        return deleted;
    }

    public List<Path> getUnversioned() {
        return unversioned;
    }
}
