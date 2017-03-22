package Application;

import MyGitLibrary.Exceptions.*;
import MyGitLibrary.MyGitObjects.RepositoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple console application that wraps work with MyGitLibrary.
 * Start the application with argument "help" to see list of commands.
 */
public class Main {

    private static String[] arguments = {"init", "add", "branch", "merge", "commit", "remove_repository",
            "remove_branch", "checkout", "log"};

    public static void main(String[] args) {
        Path directory = Paths.get(System.getProperty("user.dir"));

        if (args.length == 0) {
            System.out.println("Provide some arguments.");
            return;
        }

        if (args[0].equals("help")) {
            System.out.println(
                    "init - initializes MyGit in current directory\n" +
                            "remove_repository - remove repository in current directory\n" +
                            "add \'path\' - add current version of file contained in \'path\' to repository\n" +
                            "commit \'message\' - commit added files to current branch\n" +
                            "branch - show name of current branch" +
                            "branch \'title\' - create a new branch with name \'title\'\n" +
                            "remove_branch \'title\' - remove branch with name \'title\'\n" +
                            "merge \'title\' - merge branch with name \'title\' into current branch\n" +
                            "checkout \'title\'- checkout branch or commit with name \'title\'\n" +
                            "log - show list of commits in current branch"
            );
            return;
        }

        boolean gotArg = false;
        for (String arg : arguments) {
            if (arg.equals(args[0])) {
                gotArg = true;
            }
        }
        if (!gotArg) {
            System.out.println("Unknown command.");
            return;
        }

        if (args[0].equals("init")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
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
            return;
        }

        if (args[0].equals("remove_repository")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                RepositoryManager.removeRepository(directory);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            }
            return;
        }

        RepositoryManager repositoryManager;

        try {
            repositoryManager = RepositoryManager.getRepositoryManager(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" +
                    "Check permissions and try again.");
            e.printStackTrace();
            return;
        } catch (RepositoryWasNotInitializedException e) {
            System.out.println("RepositoryManager in this directory wasn't initialized.");
            return;
        } catch (MyGitFilesAreBrokenException e) {
            System.out.println("MyGit files are broken.");
            return;
        }

        if (args[0].equals("add")) {
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
            return;
        }

        if (args[0].equals("checkout")) {
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
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            }

            return;
        }


        if (args[0].equals("commit")) {
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
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            }

            return;
        }

        if (args[0].equals("branch")) {
            if (args.length == 1) {
                try {
                    System.out.println(repositoryManager.getCurrentBranchesName());
                } catch (IOException e) {
                    System.out.println("Something went wrong during reading or writing to files.\n" +
                            "Check permissions and try again.");
                    e.printStackTrace();
                } catch (HeadFileIsBrokenException e) {
                    System.out.println(".mygit/HEAD file is broken.");
                }
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                repositoryManager.createBranch(args[1]);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            } catch (BranchAlreadyExistsException e) {
                System.out.println("Branch with the name \"" + args[1] + "\" already exists.");
            } catch (HeadFileIsBrokenException e) {
                System.out.println(".mygit/HEAD file is broken.");
            }
            return;
        }

        if (args[0].equals("remove_branch")) {
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
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            } catch (NotAbleToDeleteCurrentBranchException e) {
                System.out.println("You can't delete current branch.");
            } catch (HeadFileIsBrokenException e) {
                System.out.println(".mygit/HEAD file is broken.");
            }

            return;
        }

        if (args[0].equals("merge")) {
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
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            } catch (BranchDoesntExistException e) {
                System.out.println("There is no branch with name \"" + args[1] + "\"");
            } catch (HeadFileIsBrokenException e) {
                System.out.println(".mygit/HEAD file is broken.");
            }

            return;
        }

        if (args[0].equals("log")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.log();
            } catch (HeadFileIsBrokenException e) {
                System.out.println(".mygit/HEAD file is broken.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" +
                        "Check permissions and try again.");
                e.printStackTrace();
            }

        }
    }
}