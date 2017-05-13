package MyGitLibrary.MyGitObjects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents log. It contains current branch's name and list of commits in chronological order.
 */
public class LogObject {

    private String branchName;
    private List<LogCommitObject> commits = new ArrayList<>();

    /**
     * Creates new log object from list of commits and name of current branch.
     * @param commits - list of commits in chronological order.
     * @param branchName - name of current branch.
     */
    LogObject(@NotNull List<Commit> commits, @NotNull String branchName) {
        this.commits.addAll(commits.stream().map(LogCommitObject::new).collect(Collectors.toList()));
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public List<LogCommitObject> getCommits() {
        return commits;
    }
}
