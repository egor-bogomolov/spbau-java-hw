import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Implementations of this interface can compute MD5-check-sum of a file or a directory (recursively).
 */
public interface MD5Provider {
    /**
     * @param path - path to a file or a directory, check-sum of which you want to compute.
     * @return - byte array with check-sum
     * @throws NoSuchAlgorithmException - this exception shouldn't be thrown unless MessageDigest has broken.
     * @throws IOException - thrown when something went wrong during input/output.
     */
    byte[] getMD5(@NotNull Path path) throws NoSuchAlgorithmException, IOException;
}