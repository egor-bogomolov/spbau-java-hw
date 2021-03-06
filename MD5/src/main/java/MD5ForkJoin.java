import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Multi-threaded version of MD5Provider. Uses ForkJoinPool.
 */

public class MD5ForkJoin implements MD5Provider {

    /**
     * @param path - path to a file or a directory, check-sum of which you want to compute.
     * @return - byte array with check-sum
     * @throws NoSuchAlgorithmException - this exception shouldn't be thrown unless MessageDigest has broken.
     * @throws IOException - thrown when something went wrong during input/output.
     */
    @Override
    public byte[] getMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        RecursiveMD5Task task = new RecursiveMD5Task(path);
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(task);
    }

    private class RecursiveMD5Task extends RecursiveTask<byte[]> {

        private Path path;

        RecursiveMD5Task(@NotNull Path path) {
            this.path = path;
        }

        @Override
        protected byte[] compute() {
            if (!Files.isDirectory(path)) {
                try {
                    return fileMD5();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to read file.");
                }
            } else {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                    messageDigest.update(path.getFileName().toString().getBytes());
                    List<RecursiveMD5Task> subtasks = new ArrayList<>();
                    Files.list(path).forEach(
                            (p) -> {
                                subtasks.add(new RecursiveMD5Task(p));
                            }
                    );
                    for (RecursiveMD5Task subtask : subtasks) {
                        subtask.fork();
                    }
                    for (RecursiveMD5Task subtask : subtasks) {
                        messageDigest.update(subtask.join());
                    }
                    return messageDigest.digest();
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to read file.");
                }
            }
            return new byte[0];
        }

        private byte[] fileMD5() throws NoSuchAlgorithmException, IOException {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            InputStream inputStream = Files.newInputStream(path);

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, readBytes);
            }

            return messageDigest.digest();
        }
    }
}
