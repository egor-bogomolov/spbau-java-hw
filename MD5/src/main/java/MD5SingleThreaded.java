import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5SingleThreaded implements MD5Provider {

    private byte[] fileMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
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

    private byte[] dirMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(path.getFileName().toString().getBytes());
        System.out.println("Directory = " + path.getFileName());
        Files.list(path).forEach(
                (p) -> {
                    if (Files.isDirectory(p)) {
                        try {
                            messageDigest.update(dirMD5(p));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            messageDigest.update(fileMD5(p));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return messageDigest.digest();
    }

    @Override
    public byte[] getMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        if (!Files.isDirectory(path)) {
            return fileMD5(path);
        } else {
            return dirMD5(path);
        }
    }
}
