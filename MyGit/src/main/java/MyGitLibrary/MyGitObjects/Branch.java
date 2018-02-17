package MyGitLibrary.MyGitObjects;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that represents a branch.
 */
class Branch implements MyGitObject, Serializable {

    private String root;
    private String hash;
    private String name;
    private String commit;

    @Override
    public String getType() {
        return BRANCH;
    }

    @Override
    public String getHash() {
        return hash;
    }

    String getName() {
        return name;
    }

    String getCommitHash() {
        return commit;
    }

    Branch(@NotNull Path root, @NotNull String name, @NotNull String commit) throws IOException {
        this.root = root.toString();
        this.name = name;
        this.commit = commit;
        updateHash();
        MyGitObject.write(this, root);
    }

    void setCommit(@NotNull String commit) throws IOException {
        this.commit = commit;
        updateHash();
        MyGitObject.write(this, Paths.get(root));
    }

    private void updateHash() {
        hash = DigestUtils.sha1Hex((name + commit).getBytes());
    }
}
