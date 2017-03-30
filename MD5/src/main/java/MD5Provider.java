import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface MD5Provider {
    byte[] getMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException;
}