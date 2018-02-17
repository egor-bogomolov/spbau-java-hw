package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import MyGitLibrary.Exceptions.FileIOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that represents a commit.
 */
class Commit implements MyGitObject, Serializable, Comparable<Commit> {

    private String root;
    private String message;
    private String author;
    private Date date;
    private List<String> parents;
    private Tree tree;
    private String hash;

    @Override
    public String getType() {
        return COMMIT;
    }

    @Override
    public String getHash() {
        return hash;
    }

    private Commit(@NotNull Path root, @NotNull String message, @NotNull String author, @NotNull Date date,
                  @NotNull List<String> parents, @NotNull Tree tree) throws FileIOException {
        this.root = root.toString();
        this.message = message;
        this.author = author;
        this.date = date;
        this.parents = parents;
        this.tree = tree;
        updateHash();
        MyGitObject.write(this, root);
    }

    Commit(@NotNull Path root, @NotNull String message, @NotNull List<String> parents,
                  @NotNull Tree tree) throws FileIOException {
        this(root, message, System.getProperty("user.name"), new Date(), parents, tree);
    }

    Commit(@NotNull Path root, @NotNull String message, @NotNull List<String> parents)
            throws FileIOException {
        this(root, message, System.getProperty("user.name"), new Date(), parents,
                new Tree(root, root.getName(root.getNameCount() - 1).toString(), new ArrayList<>()));
    }

    String getMessage() {
        return message;
    }

    String getAuthor() {
        return author;
    }

    Date getDate() {
        return date;
    }

    Tree getTree() {
        return tree;
    }

    List<Commit> getLog() throws FileIOException, ClassNotFoundException {
        List<Commit> result = new ArrayList<>();
        result.add(this);
        for (String hashParent : parents) {
            Commit parent = (Commit) MyGitObject.read(
                    Paths.get(root).resolve(Constants.objectsDirectory).resolve(hashParent));
            result.addAll(parent.getLog());
        }
        return result;
    }

    private void updateHash() {
        StringBuilder content = new StringBuilder();
        content.append(message);
        content.append(author);
        content.append(date);
        content.append(parents);
        content.append(tree.getHash());
        parents.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }

    @Override
    public int compareTo(@NotNull Commit that) {
        return this.getDate().compareTo(that.getDate());
    }
}
