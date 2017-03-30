import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MD5ForkJoin implements MD5Provider {

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
                } catch (Exception e) {}
            } else {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                    messageDigest.update(path.getFileName().toString().getBytes());
                    System.out.println("Directory = " + path.getFileName());
                    Files.list(path).forEach(
                            (p) -> {
                                RecursiveMD5Task subtask = new RecursiveMD5Task(p);
                                subtask.fork();
                                messageDigest.update(subtask.join());
                            }
                    );
                    return messageDigest.digest();
                } catch(Exception e) {}
            }
            return new byte[0];
        }

        private byte[] fileMD5() throws NoSuchAlgorithmException, IOException {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            InputStream inputStream = Files.newInputStream(path);

            System.out.println("File = " + path.getFileName());

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, readBytes);
            }

            return messageDigest.digest();
        }
    }
}
