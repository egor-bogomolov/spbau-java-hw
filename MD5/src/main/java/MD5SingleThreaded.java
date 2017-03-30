import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Single-threaded version of MD5Provider.
 */
public class MD5SingleThreaded implements MD5Provider {

    /**
     * @param path - path to a file or a directory, check-sum of which you want to compute.
     * @return - byte array with check-sum
     * @throws NoSuchAlgorithmException - this exception shouldn't be thrown unless MessageDigest has broken.
     * @throws IOException - thrown when something went wrong during input/output.
     */
    @Override
    public byte[] getMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        if (!Files.isDirectory(path)) {
            return fileMD5(path);
        } else {
            return dirMD5(path);
        }
    }

    private byte[] fileMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        InputStream inputStream = Files.newInputStream(path);

        byte[] buffer = new byte[1024];
        int readBytes;
        while ((readBytes = inputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, readBytes);
        }

        return messageDigest.digest();
    }

    private byte[] dirMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(path.getFileName().toString().getBytes());
        Files.list(path).forEach(
                (p) -> {
                    if (Files.isDirectory(p)) {
                        try {
                            messageDigest.update(dirMD5(p));
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Unable to read file.");
                        }
                    } else {
                        try {
                            messageDigest.update(fileMD5(p));
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Unable to read file.");
                        }
                    }
                }
        );
        return messageDigest.digest();
    }

}
