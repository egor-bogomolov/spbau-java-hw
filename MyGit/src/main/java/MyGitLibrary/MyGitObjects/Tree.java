package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a directory in hierarchy of VCS.
 */
class Tree implements MyGitObject, Serializable {

    private List<String> children;
    private String directoryName;
    private String hash;
    private String root;

    @Override
    public String getType() {
        return TREE;
    }

    @Override
    public String getHash() {
        return hash;
    }

    private String getDirectoryName() {
        return directoryName;
    }

    Tree(@NotNull Path root, @NotNull String directoryName, @NotNull List<String> children)
            throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        this.children = children;
        updateHash();
        MyGitObject.write(this, root);
    }

    private Tree(@NotNull Path root, @NotNull String directoryName)
            throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        children = new ArrayList<>();
        updateHash();
        MyGitObject.write(this, root);
    }

    /**
     * This method takes path to the file and file's hash and returns a Tree that is equal to the Tree in which
     * the method was called with added file.
     * @param path - path to the file that should be added.
     * @param hash - hash of file.
     * @return - new Tree, that is equal to this one with added file.
     * @throws IOException - thrown if something went wrong during input or output.
     */
    Tree addPathToTree(@NotNull Path path, @NotNull String hash) throws IOException, ClassNotFoundException {
        if (path.getNameCount() == 0) {
            throw new IllegalArgumentException();
        }
        if (path.getNameCount() == 1) {
            Blob blob = (Blob) getChild(hash);
            List<String> newChildren = new ArrayList<>();
            for (String childHash : children) {
                MyGitObject child = getChild(childHash);
                if (!child.getType().equals(MyGitObject.BLOB) ||
                        !((Blob)child).getFileName().equals(blob.getFileName())) {
                    newChildren.add(childHash);
                }
            }
            newChildren.add(hash);
            return new Tree(Paths.get(root), directoryName, newChildren);
        } else {
            List<String> newChildren = new ArrayList<>();
            boolean foundInTree = false;
            String directory = path.getName(0).toString();
            for (String childHash : children) {
                MyGitObject child = getChild(childHash);
                if (child.getType().equals(MyGitObject.TREE) &&
                        ((Tree)child).getDirectoryName().equals(directory)) {
                    foundInTree = true;
                    newChildren.add(((Tree) child)
                            .addPathToTree(path.subpath(1, path.getNameCount()), hash)
                            .getHash());
                } else {
                    newChildren.add(childHash);
                }
            }
            if (!foundInTree) {
                newChildren.add(new Tree(Paths.get(root), directory)
                .addPathToTree(path.subpath(1, path.getNameCount()), hash)
                .getHash());
            }
            return new Tree(Paths.get(root), directoryName, newChildren);
        }
    }

    /**
     * This method recursively constructs list of all paths to files contained in this Tree and it's children and
     * files' hashes.
     * @param currentPath - path to the Tree from the root.
     * @return - list of pairs consisting of path to file and it's hash. This list should be checked out.
     * @throws IOException - thrown if something went wrong during input or output.
     */
    List<PairPathString> checkoutTree(@NotNull Path currentPath) throws IOException, ClassNotFoundException {
        List<PairPathString> files = new ArrayList<>();
        for (String childHash : children) {
            MyGitObject child = getChild(childHash);
            if (child.getType().equals(BLOB)) {
                Path filePath = currentPath.resolve(((Blob) child).getFileName());
                OutputStream outputStream = Files.newOutputStream(filePath);
                outputStream.write(((Blob) child).getContent());
                outputStream.close();
                files.add(new PairPathString(filePath, childHash));
            } else {
                Path nextDirectory = currentPath.resolve(((Tree) child).getDirectoryName());
                if (Files.notExists(nextDirectory)) {
                    Files.createDirectory(nextDirectory);
                }
                files.addAll(((Tree) child).checkoutTree(nextDirectory));
            }
        }
        return files;
    }

    private MyGitObject getChild(String childHash) throws IOException, ClassNotFoundException {
        return MyGitObject.read(Paths.get(root).resolve(Constants.objectsDirectory).resolve(childHash));
    }

    private void updateHash() {
        StringBuilder content = new StringBuilder();
        content.append(directoryName);
        children.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }
}
