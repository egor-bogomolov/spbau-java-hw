package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import MyGitLibrary.Exceptions.FileDoesntExistException;
import MyGitLibrary.Exceptions.IsDirectoryException;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    /**
     * This method recursively walks commit tree and identifies status of files in it.
     * @param curPath - path to directory, represented by this Tree.
     * @param processed - Set of files that were already processed. For example, files that are staged for commit
     *                  and contained in index.
     * @param status - StatusObject in which files should be added.
     * @throws IOException - thrown if something went wrong during input or output.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    void updateStatus(@NotNull Path curPath, @NotNull Set<Path> processed, @NotNull StatusObject status)
            throws IOException, ClassNotFoundException {
        for (String childHash : children) {
            MyGitObject child = getChild(childHash);
            if (child.getType().equals(MyGitObject.TREE)) {
                ((Tree) child).updateStatus(curPath.resolve(((Tree) child).getDirectoryName()), processed, status);
            } else {
                Path path = curPath.resolve(Paths.get(((Blob) child).getFileName()));
                if (processed.contains(path)) {
                    continue;
                }
                processed.add(path);
                if (Files.exists(path)) {
                    byte[] content = Files.readAllBytes(path);
                    if (Arrays.equals(content, ((Blob) child).getContent())) {
                        status.addUnmodified(path);
                    } else {
                        status.addModified(path);
                    }
                } else {
                    status.addDeleted(path);
                }
            }
        }
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
