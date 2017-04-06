package Application;

import MyGitLibrary.Exceptions.*;
import MyGitLibrary.MyGitObjects.LogCommitObject;
import MyGitLibrary.MyGitObjects.LogObject;
import MyGitLibrary.MyGitObjects.RepositoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        branch("- show name of current branch"),
        merge("\'title\' - merge branch with name \'title\' into current branch"),
        commit("\'message\' - commit added files to current branch"),
        remove_repository("- remove repository in current directory"),
        remove_branch("\'title\' - remove branch with name \'title\'"),
        checkout("\'title\'- checkout branch or commit with name \'title\'"),
        log("- show list of commits in current branch"),
        help("- print help");

        private String description;

        Arguments(String description) {
            this.description = description;
        }
    }

    private static void checkCorrectness(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide some arguments.");
            System.exit(0);
        }
        try {
            Arguments.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown command.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        checkCorrectness(args);

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
                getRepositoryManager();
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
        }
    }

    private static void commandInit(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments.");
        }
        try {
            RepositoryManager.initRepository(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        } catch (RepositoryAlreadyExistsException e) {
            System.out.println("RepositoryManager already exists in this directory.");
        }
    }

    private static void commandRemoveRepository(String[] args) {
        if (args.length > 1) {
            System.out.println("Too many arguments.");
        }
        try {
            RepositoryManager.removeRepository(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        }
    }

    private static void getRepositoryManager() {
        try {
            repositoryManager = RepositoryManager.getRepositoryManager(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
            System.exit(0);
        } catch (RepositoryWasNotInitializedException e) {
            System.out.println("RepositoryManager in this directory wasn't initialized.");
            System.exit(0);
        } catch (MyGitFilesAreBrokenException e) {
            System.out.println("MyGit files are broken.");
            System.exit(0);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            if (e.getSuppressed().length > 0) {
                e.getSuppressed()[0].printStackTrace();
            }
            System.exit(0);
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
            repositoryManager.add(Paths.get(args[1]));
        } catch (FileInAnotherDirectoryException e) {
            System.out.println("You're trying to add file from another directory.");
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        }
    }

    private static void commandBranch(String[] args) {
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        } catch (NotAbleToDeleteCurrentBranchException e) {
            System.out.println("You can't delete current branch.");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        } catch (BranchDoesntExistException e) {
            System.out.println("There is no branch with name \"" + args[1] + "\"");
        } catch (HeadFileIsBrokenException e) {
            System.out.println(".mygit/HEAD file is broken.");
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        for (Arguments arg : Arguments.values()) {
            System.out.println(arg.toString() + " " + arg.description);
        }
    }
}