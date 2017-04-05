package MyGitLibrary.MyGitObjects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LogObject {

    private String branchName;
    private List<LogCommitObject> commits = new ArrayList<>();

    LogObject(@NotNull List<Commit> commits, @NotNull String branchName) {
        for (Commit commit : commits) {
            this.commits.add(new LogCommitObject(commit));
        }
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public List<LogCommitObject> getCommits() {
        return commits;
    }
}
