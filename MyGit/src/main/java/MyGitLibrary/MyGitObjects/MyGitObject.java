package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface represents the most common version of an object in VCS.
 * It was made to serialize/deserialize all the objects with two methods and avoid duplications.
 */
interface MyGitObject extends Serializable {

    /**
     * Identifiers of different types of objects.
     */
    String BLOB = "blob";
    String TREE = "tree";
    String COMMIT = "commit";
    String BRANCH = "branch";

    /**
     * @return - string, identifying type of the object.
     */
    String getType();

    /**
     * @return - string, representing sha-1 hash of the object.
     */
    String getHash();

    /**
     * MyGitObject implements serializable interface. It allows writing instances of MyGitObject to files
     * and reading from them with universal methods.
     * @param object - object that should be written.
     * @param path - path to the file in which the object should be written.
     * @throws IOException - thrown if something went wrong during input or output.
     */
    static void write(@NotNull MyGitObject object, @NotNull Path path) throws IOException {
        OutputStream fileOutputStream;
        if (object.getType().equals(BRANCH)) {
            fileOutputStream = Files.newOutputStream(
                    path.resolve(Constants.branchesDirectory).resolve(((Branch)object).getName()));
        } else {
            fileOutputStream = Files.newOutputStream(
                    path.resolve(Constants.objectsDirectory).resolve(object.getHash()));
        }
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(object);
        outputStream.close();
        fileOutputStream.close();
    }

    /**
     * MyGitObject implements serializable interface. It allows writing instances of MyGitObject to files
     * and reading from them with universal methods.
     * @param path - path to the file, from which an instance of MyGitObject should be read.
     * @return - an instance of MyGitObject.
     * @throws IOException - thrown if something went wrong during input or output.
     * @throws ClassNotFoundException - normally shouldn't be thrown.
     */
    @NotNull static MyGitObject read(@NotNull Path path) throws IOException, ClassNotFoundException {
        InputStream fileInputStream = Files.newInputStream(path);
        ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
        MyGitObject object = (MyGitObject) inputStream.readObject();
        inputStream.close();
        fileInputStream.close();
        return object;
    }
}
