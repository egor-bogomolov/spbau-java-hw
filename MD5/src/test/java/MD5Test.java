import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class MD5Test {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path root;

    @Before
    public void setup() throws Exception {
        byte[] byte1 = new byte[10];
        byte[] byte2 = new byte[10];
        for (int i = 0; i < 10; i++) {
            byte1[i] = 0;
            byte2[i] = 1;
        }
        root = folder.getRoot().toPath();
        Files.createFile(root.resolve("file"));
        OutputStream outputStream = Files.newOutputStream(root.resolve("file"));
        outputStream.write(byte1);
        outputStream.close();
        Files.createDirectory(root.resolve("dir"));
        Files.createFile(root.resolve("dir").resolve("file"));
        outputStream = Files.newOutputStream(root.resolve("dir").resolve("file"));
        outputStream.write(byte2);
        outputStream.close();
    }

    @Test
    public void singleThreadedNotFailing() throws IOException, NoSuchAlgorithmException {
        MD5Provider provider = new MD5SingleThreaded();
        provider.getMD5(root);
    }

    @Test
    public void multiThreadedNotFailing() throws IOException, NoSuchAlgorithmException {
        MD5Provider provider = new MD5ForkJoin();
        provider.getMD5(root);
    }

    @Test
    public void sameResultOfBothProviders() throws IOException, NoSuchAlgorithmException {
        MD5Provider provider1 = new MD5SingleThreaded();
        MD5Provider provider2 = new MD5ForkJoin();
        byte[] byte1 = provider1.getMD5(root);
        byte[] byte2 = provider2.getMD5(root);
        assertArrayEquals(byte1, byte2);
    }
}