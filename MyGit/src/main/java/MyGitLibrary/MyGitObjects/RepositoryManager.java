package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import MyGitLibrary.Exceptions.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a repository and provides access to all functions that work with it.
 * External work with library executes through instances of this class.
 * All paths to files should be absolute.
 */
public class RepositoryManager {

    private enum HeadType { COMMIT, BRANCH }

    private Path root;
    private List<Branch> branches = new ArrayList<>();
    private Logger logger;

    private RepositoryManager(@NotNull Path path) {
        root = path;
        logger = LoggerBuilder.getLogger(getLogsDir());
        logger.trace("Creating RepositoryManager for directory " + root + "...\n");
    }

    /**
     * Creates a repository in given directory, makes initial commit and sets branch to "master".
     * @param path - directory in which the repository should be created.
     * @return - a manager that represents created repository.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws DirIOException - thrown if something went wrong during input/output to/from file.
     * @throws RepositoryAlreadyExistsException - thrown if a repository already exists in given directory.
     */
    public static RepositoryManager initRepository(@NotNull Path path)
            throws FileIOException, RepositoryAlreadyExistsException, DirIOException {
        if (Files.exists(path.resolve(Constants.myGitDirectory))) {
            throw new RepositoryAlreadyExistsException();
        }
        try {
            Files.createDirectory(path.resolve(Constants.myGitDirectory));
        } catch (IOException e) {
            throw new DirIOException(path.resolve(Constants.myGitDirectory).toString());
        }
        try {
            Files.createDirectory(path.resolve(Constants.objectsDirectory));
        } catch (IOException e) {
            throw new DirIOException(path.resolve(Constants.objectsDirectory).toString());
        }
        try {
            Files.createDirectory(path.resolve(Constants.branchesDirectory));
        } catch (IOException e) {
            throw new DirIOException(path.resolve(Constants.branchesDirectory).toString());
        }
        try {
            Files.createFile(path.resolve(Constants.index));
        } catch (IOException e) {
            throw new FileIOException(path.resolve(Constants.index).toString());
        }try {
            Files.createFile(path.resolve(Constants.head));
        } catch (IOException e) {
            throw new FileIOException(path.resolve(Constants.head).toString());
        }

        RepositoryManager repositoryManager = new RepositoryManager(path);
        repositoryManager.initialCommit();
        repositoryManager.logger.trace("RepositoryManager for directory " + path + " was created\n");
        return repositoryManager;
    }

    /**
     * Deletes repository in given directory.
     * @param path - directory in which the repository should be removed.
     * @throws DirIOException - thrown if something went wrong during deleting directoryЯ.
     */
    public static void removeRepository(@NotNull Path path) throws DirIOException {
        try {
            FileUtils.deleteDirectory(path.resolve(Constants.myGitDirectory).toFile());
        } catch (IOException e) {
            throw new DirIOException(path.resolve(Constants.myGitDirectory).toString());
        }
    }

    /**
     * Creates a manager representing repository in given directory. Repository should be initialized already.
     * @param path - directory containing a repository.
     * @return - a manager that represents repository.
     * @throws RepositoryWasNotInitializedException - thrown if there is no repository in given directory.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws WalkIOException - thrown if something went wrong during walking from directory.
     * @throws MyGitFilesAreBrokenException - thrown when something happened to MyGit files, for example, they
     * were changed manually.
     */
    public static RepositoryManager getRepositoryManager(@NotNull Path path)
            throws RepositoryWasNotInitializedException, FileIOException,
            MyGitFilesAreBrokenException, ClassNotFoundException, NotDirectoryException, WalkIOException {
        if (!Files.isDirectory(path)) {
            throw new NotDirectoryException(path.toString());
        }
        Path myGitDir = path.resolve(Constants.myGitDirectory);
        Path objDir = path.resolve(Constants.objectsDirectory);
        Path branchesDir = path.resolve(Constants.branchesDirectory);
        Path indexFile = path.resolve(Constants.index);
        Path headFile = path.resolve(Constants.head);
        if (Files.notExists(myGitDir) || !Files.isDirectory(myGitDir)) {
            throw new RepositoryWasNotInitializedException();
        }
        if (Files.notExists(objDir) || !Files.isDirectory(objDir)
                || Files.notExists(branchesDir) || !Files.isDirectory(branchesDir)
                || Files.notExists(indexFile) || Files.isDirectory(indexFile)
                || Files.notExists(headFile) || Files.isDirectory(headFile)){
            throw new MyGitFilesAreBrokenException();
        }
        final RepositoryManager repositoryManager = new RepositoryManager(path);
        try {
            List<Path> paths = Files.walk(branchesDir).collect(Collectors.toList());
            for (Path file : paths) {
                if (!Files.isDirectory(file)) {
                    Branch branch = (Branch) MyGitObject.read(file);
                    repositoryManager.addBranch(branch);
                }
            }
        } catch (IOException e) {
            throw new WalkIOException(branchesDir.toString());
        }
        repositoryManager.logger.trace("RepositoryManager for directory " + path + " was created\n");
        return repositoryManager;
    }

    /**
     * Adds given file to repository. Current version of the file will be saved in next commit, unless you
     * checkout something before the commit.
     * @param path - file that should be added.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws FileInAnotherDirectoryException - thrown if a file is in another directory.
     * @throws FileDoesntExistException - thrown if a file that should be added doesn't exist.
     * @throws IsDirectoryException - thrown if a directory instead of file was provided.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     */
    public void add(@NotNull Path path) throws FileIOException,
            FileInAnotherDirectoryException, FileDoesntExistException,
            IsDirectoryException, IndexFileIsBrokenException {
        logger.trace("Trying to add file " + path + "\n");
        if (!path.startsWith(root)) {
            logger.trace("File " + path + " wasn't added because of user's mistake\n");
            throw new FileInAnotherDirectoryException();
        }
        if (!Files.exists(path)) {
            logger.trace("File " + path + " wasn't added because of user's mistake\n");
            throw new FileDoesntExistException();
        }
        if (Files.isDirectory(path)) {
            logger.trace("File " + path + " wasn't added because of user's mistake\n");
            throw new IsDirectoryException();
        }

        Blob blob;
        try {
            blob = new Blob(root, Files.readAllBytes(path), path.getFileName().toString());
        } catch (IOException e) {
            throw new FileIOException(path.toString());
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(getIndex());
        } catch (IOException e) {
            throw new FileIOException(getIndex().toString());
        }
        String hash = blob.getHash();
        String file = "";
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                logger.trace("File " + path + " wasn't added because of broken index file\n");
                throw new IndexFileIsBrokenException();
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file += line + "\n";
        }
        file += path + " " + hash + "\n";

        try {
            OutputStream outputStream = Files.newOutputStream(getIndex());
            outputStream.write(file.getBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new FileIOException(getIndex().toString());
        }
        logger.trace("File " + path + " was added\n");
    }

    /**
     * Commits changes that were added after the last commit/checkout. Author and date are saved automatically.
     * @param message - text that goes with the commit.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void commit(@NotNull String message)
            throws FileIOException, IndexFileIsBrokenException,
            HeadFileIsBrokenException, ClassNotFoundException {
        logger.trace("Commit with message \'" + message + "\'\n");
        List<String> lines;
        try {
            lines = Files.readAllLines(getIndex());
        } catch (IOException e) {
            throw new FileIOException(getIndex().toString());
        }
        List<PairPathString> pathsAndHashes = new ArrayList<>();
        for (String line : lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                logger.trace("Commit unsuccessful because of broken index file\n");
                throw new IndexFileIsBrokenException();
            }
            pathsAndHashes.add(new PairPathString(Paths.get(strings[0]), strings[1]));
        }
        Tree tree = buildCommitTree(pathsAndHashes);
        List<String> parents = new ArrayList<>();
        parents.add(getHeadCommit().getHash());
        Commit commit = new Commit(root, message, parents, tree);
        getHeadBranch().setCommit(commit.getHash());
        writeToHead(commit.getHash());
        clearIndex();
        logger.trace("Commit " +  commit.getHash() + " successful\n");
    }

    /**
     * Checkouts a commit or the last commit of a branch. It means that all the files in directory that were
     * also saved in that commit are replaced with their versions from commit. All adds that weren't commited
     * will be erased. If you checkout commit, a new branch with commit's name will be created. It was done to
     * simplify following work with VCS.
     * @param name - name of a branch or a commit that you want to checkout.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws DirIOException - thrown if something went wrong during creating directory.
     * @throws FileDoesntExistException - thrown if there is no branch or commit with given name.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void checkout(@NotNull String name) throws FileIOException, FileDoesntExistException,
            ClassNotFoundException, DirIOException {
        logger.trace("Checkout " + name + "\n");
        Branch branch = getBranch(name);
        if (branch == null) {
            if (Files.notExists(getObjectsDir().resolve(name))) {
                logger.trace("Checkout " + name + " failed because such commit or branch doesn't exist\n");
                throw new FileDoesntExistException();
            }
            branch = new Branch(root, name, name);
            branches.add(branch);
            logger.trace("Checkout commit\n");
        } else {
            logger.trace("Checkout branch\n");
        }
        checkoutCommit(branch.getCommitHash());
        writeToHead(branch);
        clearIndex();
        logger.trace("Checkout " + name + " successful\n");
    }

    /**
     * Creates new branch with given name. It will copy current state of current branch.
     * @param name - name of new branch.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws BranchAlreadyExistsException - thrown if a branch with given name already exists.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void createBranch(@NotNull String name) throws FileIOException, BranchAlreadyExistsException,
            HeadFileIsBrokenException, ClassNotFoundException {
        if (getBranch(name) != null) {
            logger.trace("Branch with name " + name + " already exists\n");
            throw new BranchAlreadyExistsException();
        }branches.add(new Branch(root, name, getHeadCommit().getHash()));
        logger.trace("Created branch with name " + name + " successfully\n");
    }

    /**
     * Deletes branch with given name. It's impossible to delete current branch. You should switch
     * branches beforehand.
     * @param name - name of branch to delete.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws NotAbleToDeleteCurrentBranchException - thrown if you're trying to delete current branch.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void removeBranch(@NotNull String name) throws FileIOException,
            NotAbleToDeleteCurrentBranchException, HeadFileIsBrokenException, ClassNotFoundException {
        if (getHeadBranch().getName().equals(name)) {
            throw new NotAbleToDeleteCurrentBranchException();
        }
        Branch branch = getBranch(name);
        try {
            Files.deleteIfExists(getBranchesDir().resolve(name));
        } catch (IOException e) {
            throw new FileIOException(getBranchesDir().resolve(name).toString());
        }
        if (branch != null) {
            branches.remove(branch);
            logger.trace("Removed branch with name " + name + " successfully\n");
        }
    }

    /**
     * Merges the branch with given name into current branch and instantly checkouts result. When both branches
     * contain the file, current branch has priority. It means that last version of the file in current branch
     * will be saved.
     * @param name - branch that should be merged into current one.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws DirIOException - thrown if something went wrong during creating directory.
     * @throws BranchDoesntExistException - thrown if a branch with given name doesn't exist.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void merge(@NotNull String name) throws FileIOException, BranchDoesntExistException,
            HeadFileIsBrokenException, ClassNotFoundException, DirIOException {
        logger.trace("Merging branch with name " + name + " into current branch\n");
        Branch currentBranch = getHeadBranch();
        Branch secondBranch = getBranch(name);
        if (secondBranch == null) {
            logger.trace("Merging failed because branch doesn't exist\n");
            throw new BranchDoesntExistException();
        }
        if (currentBranch.getName().equals(secondBranch.getName())) {
            logger.trace("Merging failed because branch doesn't exist\n");
            return;
        }
        Commit currentCommit = (Commit) MyGitObject.read(getObjectsDir().resolve(currentBranch.getCommitHash()));
        Commit secondCommit = (Commit) MyGitObject.read(getObjectsDir().resolve(secondBranch.getCommitHash()));

        List<String> parents = new ArrayList<>();
        parents.add(currentCommit.getHash());
        parents.add(secondCommit.getHash());

        List<PairPathString> files2 = secondCommit.getTree().checkoutTree(root);
        List<PairPathString> files1 = currentCommit.getTree().checkoutTree(root);
        Set<Path> filePaths = files1.stream()
                .map(PairPathString::getPath)
                .collect(Collectors.toSet());
        files1.addAll(files2.stream()
                .filter(pair -> !filePaths.contains(pair.getPath()))
                .collect(Collectors.toList()));
        Tree newCommitTree = buildCommitTree(files1);

        Commit newCommit = new Commit(root,
                "merged branch \"" + name + "\" into \"" + currentBranch.getName() + "\"",
                parents, newCommitTree);
        currentBranch.setCommit(newCommit.getHash());
        writeToHead(newCommit.getHash());
        writePairsToIndex(files1);
        logger.trace("Merged branch with name " + name + " into current branch successfully\n");
    }

    /**
     * Removes information about file contained in path from index. Thus, it won't be added on next commit.
     * @param path - path to the file that should be removed from index.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     * @throws FileInAnotherDirectoryException - thrown if a file is in another directory.
     */
    public void reset(@NotNull Path path) throws FileIOException,
            IndexFileIsBrokenException, FileInAnotherDirectoryException {
        if (!path.startsWith(root)) {
            throw new FileInAnotherDirectoryException();
        }
        logger.trace("Reset was called on file " + path + "\n");
        removeFromIndex(path);
    }

    /**
     * Removes information about file contained in path from index and deletes it from the disk. File won't be
     * added on next commit.
     * @param path - path to the file that should be removed
     * @throws FileInAnotherDirectoryException  - thrown if a file is in another directory.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     * @throws IsDirectoryException - thrown if a directory instead of file was provided.
     */
    public void remove(@NotNull Path path) throws FileInAnotherDirectoryException,
            FileIOException, IndexFileIsBrokenException, IsDirectoryException {
        logger.trace("Remove was called on file " + path + "\n");
        reset(path);
        if (Files.exists(path) && Files.isDirectory(path)) {
            logger.trace("Remove of " + path + " failed because it's a directory\n");
            throw new IsDirectoryException();
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.trace("Remove of " + path + " failed because it's broken\n");
            throw new FileIOException(path.toString());
        }
        logger.trace("Removed " + path + " successfully\n");
    }

    /**
     * Returns a log object representing log of current branch.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public LogObject log() throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        logger.trace("Creating log of branch \'" + getCurrentBranchesName() + "\'\n");
        Commit lastCommit = (Commit) MyGitObject.read(getObjectsDir().resolve(getHeadBranch().getCommitHash()));
        List<Commit> commitsInLog = lastCommit.getLog();
        List<Commit> uniqueCommits = new ArrayList<>();
        Set<String> hashes = new HashSet<>();
        for (Commit commit : commitsInLog) {
            if (!hashes.contains(commit.getHash())) {
                uniqueCommits.add(commit);
            }
            hashes.add(commit.getHash());
        }
        uniqueCommits.sort(Commit::compareTo);
        logger.trace("Log of branch \'" + getCurrentBranchesName() + "\' was created successfully\n");
        return new LogObject(uniqueCommits, getCurrentBranchesName());
    }

    /**
     * Returns StatusObject, containing statuses of all files contained in repository. There are five
     * possible statuses:
     * staged - file is staged for commit
     * unmodified - file wasn't modified since head commit
     * modified - file was changed since head commit
     * deleted - file was deleted from disk
     * unversioned - file neither staged for commit nor contained in head commit
     * @return - StatusObject with lists of files with different statuses.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws WalkIOException - thrown if something went wrong during walking from directory.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public StatusObject status() throws FileIOException, IndexFileIsBrokenException,
            HeadFileIsBrokenException, ClassNotFoundException, WalkIOException {
        logger.trace("Creating status...\n");
        StatusObject status = new StatusObject();
        Set<Path> processed = new HashSet<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(getIndex());
        } catch (IOException e) {
            logger.trace("Creating status failed because of broken index file\n");
            throw new FileIOException(getIndex().toString());
        }
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                logger.trace("Creating status failed because of broken index file\n");
                throw new IndexFileIsBrokenException();
            }
            status.addStaged(Paths.get(strings[0]));
            processed.add(Paths.get(strings[0]));
        }
        getHeadCommit().getTree().updateStatus(root, processed, status);
        try {
            List<Path> files = Files.walk(root)
                    .filter(p -> !p.startsWith(getMyGitDir()))
                    .collect(Collectors.toList());
            files.stream()
                    .filter(path -> !Files.isDirectory(path) && !processed.contains(path))
                    .forEach(status::addUnversioned);
        } catch (IOException e) {
            logger.trace("Creating status failed because of broken files in " + root + "\n");
            throw new WalkIOException(root.toString());
        }
        logger.trace("Created status successfully\n");
        return status;
    }

    /**
     * Deletes all unversioned files from disk.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws WalkIOException - thrown if something went wrong during walking from directory.
     * @throws IndexFileIsBrokenException - thrown if something happened to index file, for example
     * it was changed manually.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public void clean() throws IndexFileIsBrokenException, HeadFileIsBrokenException,
            ClassNotFoundException, FileIOException, WalkIOException {
        logger.trace("Cleaning directory " + root + "\n");
        StatusObject status = status();
        for (Path path : status.getUnversioned()) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                logger.trace("Cleaning failed because " + root + " is broken\n");
                throw new FileIOException(path.toString());
            }
        }
        logger.trace("Cleaned directory " + root + " successfully\n");
    }

    /**
     * Returns name of current branch.
     * @return name of current branch.
     * @throws FileIOException - thrown if something went wrong during input/output to/from file.
     * @throws HeadFileIsBrokenException - thrown if something happened to HEAD file, for example
     * it was changed manually.
     * @throws ClassNotFoundException - normally it shouldn't be thrown.
     */
    public String getCurrentBranchesName()
            throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        return getHeadBranch().getName();
    }

    private void initialCommit() throws FileIOException {
        Commit commit = new Commit(root, "initial commit", new ArrayList<>());
        Branch masterBranch = new Branch(root, "master", commit.getHash());
        branches.add(masterBranch);
        writeToHead(masterBranch);
    }

    private void checkoutCommit(@NotNull String commitHash)
            throws FileIOException, FileDoesntExistException, ClassNotFoundException, DirIOException {
        Commit commit = (Commit)MyGitObject.read(getObjectsDir().resolve(commitHash));
        List<PairPathString> files = commit.getTree().checkoutTree(root);
        writePairsToIndex(files);
    }

    private void writePairsToIndex(@NotNull List<PairPathString> files) throws FileIOException {
        try {
            OutputStream outputStream = Files.newOutputStream(getIndex());
            for (PairPathString pair : files) {
                outputStream.write((pair.getPath().toString() + " " + pair.getString() + "\n").getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            throw new FileIOException(getIndex().toString());
        }
    }

    private void writeToHead(@NotNull Branch branch) throws FileIOException {
        logger.trace("Writing to HEAD file...\n");
        try {
            OutputStream outputStream = Files.newOutputStream(getHead());
            outputStream.write((branch.getName() + "\n").getBytes());
            outputStream.write((branch.getCommitHash() + "\n").getBytes());
            outputStream.close();
        } catch (IOException e) {
            logger.trace("Writing failed because HEAD file is broken\n");
            throw new FileIOException(getHead().toString());
        }
        logger.trace("Wrote to HEAD file successfully\n");
    }

    private void writeToHead(@NotNull String commitHash)
            throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        logger.trace("Writing to HEAD file...\n");
        String name = getHeadBranch().getName();
        try {

            OutputStream outputStream = Files.newOutputStream(getHead());
            outputStream.write((name + "\n").getBytes());
            outputStream.write((commitHash + "\n").getBytes());
            outputStream.close();
        } catch (IOException e) {
            logger.trace("Writing failed because HEAD file is broken\n");
            throw new FileIOException(getHead().toString());
        }
        logger.trace("Wrote to HEAD file successfully\n");
    }

    private MyGitObject readFromHead(HeadType type) throws FileIOException, HeadFileIsBrokenException,
            ClassNotFoundException {
        logger.trace("Reading from HEAD file\n");
        List<String> lines;
        try {
            lines = Files.readAllLines(getHead());
        } catch (IOException e) {
            logger.trace("Reading failed because HEAD file is broken\n");
            throw new FileIOException(getHead().toString());
        }
        if (lines.size() != 2) {
            logger.trace("Reading failed because HEAD file is broken\n");
            throw new HeadFileIsBrokenException();
        }
        logger.trace("Reading from HEAD file successfully\n");
        if (type.equals(HeadType.BRANCH)) {
            return MyGitObject.read(getBranchesDir().resolve(lines.get(0)));
        } else {
            return MyGitObject.read(getObjectsDir().resolve(lines.get(1)));
        }
    }

    private Commit getHeadCommit() throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        return (Commit) readFromHead(HeadType.COMMIT);
    }

    private Branch getHeadBranch() throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        return (Branch) readFromHead(HeadType.BRANCH);
    }

    private Tree buildCommitTree(@NotNull List<PairPathString> pathsAndHashes)
            throws FileIOException, HeadFileIsBrokenException, ClassNotFoundException {
        logger.trace("Building commit tree...\n");
        Tree tree = getHeadCommit().getTree();
        for (PairPathString pair : pathsAndHashes) {
            tree = tree.addPathToTree(root.relativize(pair.getPath()), pair.getString());
        }
        logger.trace("Built commit tree successfully\n");
        return tree;
    }

    @Nullable private Branch getBranch(@NotNull String name) {
        for (Branch branch : branches) {
            if (branch.getName().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    private void clearIndex() throws FileIOException {
        logger.trace("Clearing index file...\n");
        try {
            OutputStream outputStream = Files.newOutputStream(getIndex());
            outputStream.write(new byte[0]);
            outputStream.close();
        } catch (IOException e) {
            logger.trace("Clearing failed because index file is broken\n");
            throw new FileIOException(getIndex().toString());
        }
        logger.trace("Cleared index file successfully\n");
    }

    private void removeFromIndex(@NotNull Path path) throws FileIOException, IndexFileIsBrokenException {
        logger.trace("Removing " + path + " from index file...\n");
        List<String> lines;
        try {
            lines = Files.readAllLines(getIndex());
        } catch (IOException e) {
            logger.trace("Remove failed because index file is broken\n");
            throw new FileIOException(getIndex().toString());
        }
        String file = "";
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                logger.trace("Remove failed because index file is broken\n");
                throw new IndexFileIsBrokenException();
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file += line + "\n";
        }
        try {
            OutputStream outputStream = Files.newOutputStream(getIndex());
            outputStream.write(file.getBytes());
            outputStream.close();
        } catch (IOException e) {
            logger.trace("Remove failed because index file is broken\n");
            throw new FileIOException(getIndex().toString());
        }
        logger.trace("Removed " + path + " from index file successfully\n");
    }

    private void addBranch(@NotNull Branch branch) {
        branches.add(branch);
    }

    private Path getMyGitDir() {
        return root.resolve(Constants.myGitDirectory);
    }

    private Path getObjectsDir() {
        return root.resolve(Constants.objectsDirectory);
    }

    private Path getBranchesDir() {
        return root.resolve(Constants.branchesDirectory);
    }

    private Path getLogsDir() {
        return root.resolve(Constants.logsDirectory);
    }

    private Path getIndex() {
        return root.resolve(Constants.index);
    }

    private Path getHead() {
        return root.resolve(Constants.head);
    }
}