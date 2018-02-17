package MyGitLibrary.MyGitObjects;

/**
 * This class represents one commit in log. It contains all necessary information from commit : hash, messeage,
 * author's name and date.
 */
public class LogCommitObject {

    private String hash;
    private String message;
    private String author;
    private String date;

    /**
     * Creates new log object representing one commit.
     * @param commit - commit's information should be saved.
     */
    LogCommitObject(Commit commit) {
        hash = commit.getHash();
        message = commit.getMessage();
        author = commit.getAuthor();
        date = commit.getDate().toString();
    }


    public String getHash() {
        return hash;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }
}
