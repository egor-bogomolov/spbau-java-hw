package Application;

import MyGitLibrary.Exceptions.*;
import MyGitLibrary.MyGitObjects.LogCommitObject;
import MyGitLibrary.MyGitObjects.LogObject;
import MyGitLibrary.MyGitObjects.RepositoryManager;
import MyGitLibrary.MyGitObjects.StatusObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple console application that wraps work with MyGitLibrary.
 * Start the application with argument "help" to see list of commands.
 */
public class Main {

    private static Path directory;
    private static RepositoryManager repositoryManager;

    private enum Arguments {
        init("- initializes MyGit in current directory"),
        add("\'path\' - add current version of file contained in 'path' to repository"),
        branch("\'name\' - with empty name shows name of current branch, " +
                "otherwise creates new branch with title \'name\'"),
        merge("\'title\' - merge branch with name \'title\' into current branch"),
        commit("\'message\' - commit added files to current branch"),
        remove_repository("- remove repository in current directory"),
        remove_branch("\'title\' - remove branch with name \'title\'"),
        checkout("\'title\'- checkout branch or commit with name \'title\'"),
        log("- show list of commits in current branch"),
        help("- print help"),
        reset("\'path\' - remove file contained in \'path\' from index, it won't be added on next commit"),
        rm("\'path\' - remove file contained in \'path\' from index and delete it from disk"),
        status("- show status of all files in directory. Possible statuses:\n" +
                "\tstaged - file is staged for commit\n" +
                "\tunmodified - file wasn't modified since head commit\n" +
                "\tmodified - file was changed since head commit\n" +
                "\tdeleted - file was deleted from disk\n" +
                "\tunversioned - file neither staged for commit nor contained in head commit"),
        clean("- deletes from disk all unversioned files");

        private String description;

        Arguments(String description) {
            this.description = description;
        }
    }

    private static void checkCorrectness(String[] args) throws UnableToContinueException {
        if (args.length == 0) {
            System.out.println("Provide some arguments.");
            throw new UnableToContinueException();
        }
        try {
            Arguments.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown command.");
            throw new UnableToContinueException();
        }
    }

    public static void main(String[] args) {
        try {
            checkCorrectness(args);
        } catch (UnableToContinueException e) {
            return;
        }

        directory = Paths.get(System.getProperty("user.dir"));

        switch (Arguments.valueOf(args[0])) {
            case help:
                printHelp();
                break;
            case init:
                commandInit(args);
                break;
            case remove_repository:
                commandRemoveRepository(args);
                break;
            default:
                try {
                    getRepositoryManager();
                } catch (UnableToContinueException e) {
                    return;
                }
        }

        switch (Arguments.valueOf(args[0])) {
            case add:
                commandAdd(args);
                break;
            case checkout:
                commandCheckout(args);
                break;
            case commit:
                commandCommit(args);
                break;
            case branch:
                commandBranch(args);
                break;
            case remove_branch:
                commandRemoveBranch(args);
                break;
            case merge:
                commandMerge(args);
                break;
            case log:
                commandLog(args);
                break;
            case reset:
                commandReset(args);
                break;
            case rm:
                commandRemove(args);
                break;
            case status:
                commandStatus(args);
                break;
            case clean:
                commandClean(args);
                break;
        }
    }

    private static void commandInit(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments.");
        }
        try {
            RepositoryManager.initRepository(directory);
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (RepositoryAlreadyExistsException e) {
            System.out.println("RepositoryManager already exists in this directory.");
        } catch (DirIOException e) {
            System.out.println("Unable to create directory at " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void commandRemoveRepository(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments.");
        }
        try {
            RepositoryManager.removeRepository(directory);
        } catch (DirIOException e) {
            System.out.println("Unable to delete directory at " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void getRepositoryManager() throws UnableToContinueException {
        try {
            repositoryManager = RepositoryManager.getRepositoryManager(directory);
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (RepositoryWasNotInitializedException e) {
            System.out.println("RepositoryManager in this directory wasn't initialized.");
            throw new UnableToContinueException();
        } catch (MyGitFilesAreBrokenException e) {
            System.out.println("MyGit files are broken.");
            throw new UnableToContinueException();
        } catch (NotDirectoryException e) {
            System.out.println("Pass a directory as an argument.");
        } catch (WalkIOException e) {
            System.out.println("Something went wrong during working with file in directory " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        }
    }

    private static void commandAdd(String[] args) {
        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            repositoryManager.add(getPath(args[1]));
        } catch (FileInAnotherDirectoryException e) {
            System.out.println("You're trying to add file from another directory.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (FileDoesntExistException e) {
            System.out.println("File doesn't exist.");
        } catch (IsDirectoryException e) {
            System.out.println("You can add only files.");
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        }
    }

    private static void commandCheckout(String[] args) {
        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            repositoryManager.checkout(args[1]);
        } catch (FileDoesntExistException e) {
            System.out.println("There is no branch or commit with name \"" + args[1] + "\"");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        } catch (DirIOException e) {
            System.out.println("Unable to create directory at " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void commandCommit(String[] args) {
        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            repositoryManager.commit(args[1]);
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        }
    }

    private static void commandBranch(String[] args) {
        if (args.length == 1) {
            try {
                System.out.println(repositoryManager.getCurrentBranchesName());
            } catch (FileIOException e) {
                System.out.println("Something went wrong during reading or writing to file " +
                        e.getMessage() + "\n" +
                        "Check permissions and existence of file try again.");
            } catch (HeadFileIsBrokenException e) {
                System.out.println(".mygit/HEAD file is broken.");
            } catch (ClassNotFoundException e) {
                System.out.println("Application's .jar file is broken.");
            }
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }
        try {
            repositoryManager.createBranch(args[1]);
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (BranchAlreadyExistsException e) {
            System.out.println("Branch with the name \"" + args[1] + "\" already exists.");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        }
    }

    private static void commandRemoveBranch(String[] args) {
        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            repositoryManager.removeBranch(args[1]);
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (NotAbleToDeleteCurrentBranchException e) {
            System.out.println("You can't delete current branch.");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        }
    }

    private static void commandMerge(String[] args) {
        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            repositoryManager.merge(args[1]);
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (BranchDoesntExistException e) {
            System.out.println("There is no branch with name \"" + args[1] + "\"");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        } catch (DirIOException e) {
            System.out.println("Unable to create directory at " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void commandLog(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments.");
            return;
        }

        try {
            LogObject log = repositoryManager.log();
            System.out.println("Current branch : " + log.getBranchName() + "\n");
            for (LogCommitObject commit : log.getCommits()) {
                System.out.println("commit : " + commit.getHash());
                System.out.println(commit.getMessage());
                System.out.println("Author : " + commit.getAuthor());
                System.out.println("Date : " + commit.getDate());
                System.out.println("");
            }
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        }
    }

    private static void commandReset(String[] args) {
        if (args.length < 2) {
            System.out.println("Too few arguments");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments");
            return;
        }
        try {
            repositoryManager.reset(getPath(args[1]));
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        } catch (FileInAnotherDirectoryException e) {
            System.out.println("You're trying to reset file from another directory.");
        }
    }

    private static void commandRemove(String[] args) {
        if (args.length < 2) {
            System.out.println("Too few arguments");
            return;
        }
        if (args.length > 2) {
            System.out.println("Too many arguments");
            return;
        }
        try {
            repositoryManager.remove(getPath(args[1]));
        } catch (FileInAnotherDirectoryException e) {
            System.out.println("You're trying to remove file from another directory.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        } catch (IsDirectoryException e) {
            System.out.println("You're trying to remove directory instead of file.");
        }
    }

    private static void commandStatus(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments");
            return;
        }

        try {
            StatusObject status = repositoryManager.status();
            for (Path path : status.getStaged()) {
                System.out.println(path + " staged for commit");
            }
            for (Path path : status.getUnmodified()) {
                System.out.println(path + " wasn't modified since head commit");
            }
            for (Path path : status.getModified()) {
                System.out.println(path + " was modified since head commit");
            }
            for (Path path : status.getDeleted()) {
                System.out.println(path + " was deleted");
            }
            for (Path path : status.getUnversioned()) {
                System.out.println(path + " isn't versioned");
            }
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        } catch (WalkIOException e) {
            System.out.println("Something went wrong during working with file in directory " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void commandClean(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments");
            return;
        }
        try {
            repositoryManager.clean();
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
        } catch (FileIOException e) {
            System.out.println("Something went wrong during reading or writing to file " +
                    e.getMessage() + "\n" +
                    "Check permissions and existence of file try again.");
        } catch (IndexFileIsBrokenException e) {
            System.out.println(".mygit/index file is broken.");
        } catch (ClassNotFoundException e) {
            System.out.println("Application's .jar file is broken.");
        } catch (WalkIOException e) {
            System.out.println("Something went wrong during working with file in directory " +
                    e.getMessage() + "\n" +
                    "Check permissions and try again.");
        }
    }

    private static void printHelp() {
        for (Arguments arg : Arguments.values()) {
            System.out.println(arg.toString() + " " + arg.description);
        }
    }

    private static Path getPath(@NotNull String path) {
        return Paths.get(path).toAbsolutePath().normalize();
    }
}