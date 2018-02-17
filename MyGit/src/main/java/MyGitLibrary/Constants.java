package MyGitLibrary;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that provides access to names of MyGit's files and directories.
 */
public class Constants {
    public static final Path myGitDirectory = Paths.get(".mygit");
    public static final Path objectsDirectory = myGitDirectory.resolve("MyGitObjects");
    public static final Path branchesDirectory = myGitDirectory.resolve("branches");
    public static final Path index = myGitDirectory.resolve("index");
    public static final Path head = myGitDirectory.resolve("HEAD");
    public static final Path logsDirectory = myGitDirectory.resolve("logs");
}
