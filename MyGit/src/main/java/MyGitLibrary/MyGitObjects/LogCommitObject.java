package MyGitLibrary.MyGitObjects;

public class LogCommitObject {

    private String hash;
    private String message;
    private String author;
    private String date;

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
