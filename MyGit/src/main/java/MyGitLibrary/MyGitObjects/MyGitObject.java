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

    String BLOB = "blob";
    String TREE = "tree";
    String COMMIT = "commit";
    String BRANCH = "branch";

    String getType();
    String getHash();

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

    @NotNull static MyGitObject read(@NotNull Path path) throws IOException {
        try {
            InputStream fileInputStream = Files.newInputStream(path);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            MyGitObject object = (MyGitObject) inputStream.readObject();
            inputStream.close();
            fileInputStream.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to read from file " + path.toString());
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
