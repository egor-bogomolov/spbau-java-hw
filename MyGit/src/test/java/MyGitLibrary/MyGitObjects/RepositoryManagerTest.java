package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Constants;
import MyGitLibrary.Exceptions.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class RepositoryManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path root;
    private byte[] byte1;
    private byte[] byte2;

    @Before
    public void setup() throws Exception {
        byte1 = new byte[10];
        byte2 = new byte[10];
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

    private String getHash(byte[] bytes) {
        return DigestUtils.sha1Hex(bytes);
    }

    private String getHash(Path path) throws Exception {
        return DigestUtils.sha1Hex(Files.readAllBytes(path));
    }

    @Test
    public void initRepository() throws Exception {
        RepositoryManager.initRepository(root);
        assertTrue(Files.exists(root.resolve(Constants.myGitDirectory)));
        assertTrue(Files.isDirectory(root.resolve(Constants.myGitDirectory)));
        assertTrue(Files.exists(root.resolve(Constants.branchesDirectory)));
        assertTrue(Files.isDirectory(root.resolve(Constants.branchesDirectory)));
        assertTrue(Files.exists(root.resolve(Constants.objectsDirectory)));
        assertTrue(Files.isDirectory(root.resolve(Constants.objectsDirectory)));
        assertTrue(Files.exists(root.resolve(Constants.head)));
        assertFalse(Files.isDirectory(root.resolve(Constants.head)));
        assertTrue(Files.exists(root.resolve(Constants.index)));
        assertFalse(Files.isDirectory(root.resolve(Constants.index)));
    }

    private void addCreatedFiles(RepositoryManager repositoryManager) throws Exception {
        repositoryManager.add(root.resolve("file"));
        repositoryManager.add(root.resolve("dir").resolve("file"));
    }

    @Test(expected = RepositoryAlreadyExistsException.class)
    public void initRepositoryTwoTimes() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.initRepository(root);
    }

    @Test
    public void deleteRepository() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.removeRepository(root);
        assertFalse(Files.exists(root.resolve(Constants.myGitDirectory)));
        RepositoryManager.initRepository(root);
    }

    @Test(expected = RepositoryWasNotInitializedException.class)
    public void getRepositoryManagerInNotInitialized() throws Exception {
        RepositoryManager repositoryManager = RepositoryManager.getRepositoryManager(root);
    }

    @Test(expected = FileDoesntExistException.class)
    public void addNotExistingFile() throws Exception {
        RepositoryManager repositoryManager = RepositoryManager.initRepository(root);
        repositoryManager.add(root.resolve("fil"));
    }

    @Test(expected = FileInAnotherDirectoryException.class)
    public void addFromAnotherDirectory() throws Exception {
        RepositoryManager repositoryManager = RepositoryManager.initRepository(root);
        repositoryManager.add(Paths.get("file"));
    }

    @Test(expected = IsDirectoryException.class)
    public void addDirectory() throws Exception {
        RepositoryManager repositoryManager = RepositoryManager.initRepository(root);
        repositoryManager.add(root.resolve("dir"));
    }

    @Test
    public void addSeveralFilesAndCheckIndex() throws Exception {
        RepositoryManager.initRepository(root);
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));

        List<String> lines = Files.readAllLines(root.resolve(Constants.index));
        assertEquals(2, lines.size());
        String[] line1 = lines.get(0).split(" ");
        assertEquals(2, line1.length);
        assertEquals(root.resolve("file").toString(), line1[0]);
        String[] line2 = lines.get(1).split(" ");
        assertEquals(2, line2.length);
        assertEquals(root.resolve("dir").resolve("file").toString(), line2[0]);
    }

    @Test
    public void addSeveralFilesAndCommit() throws Exception {
        RepositoryManager.initRepository(root);
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        List<String> lines = Files.readAllLines(root.resolve(Constants.index));
        assertEquals(2, lines.size());
        String[] line1 = lines.get(0).split(" ");
        assertEquals(2, line1.length);
        assertEquals(root.resolve("file").toString(), line1[0]);
        String[] line2 = lines.get(1).split(" ");
        assertEquals(2, line2.length);
        assertEquals(root.resolve("dir").resolve("file").toString(), line2[0]);

        RepositoryManager.getRepositoryManager(root).commit("first commit");
    }

    @Test(expected = FileDoesntExistException.class)
    public void checkoutNotExistingBranch() throws Exception {
        RepositoryManager repositoryManager = RepositoryManager.initRepository(root);
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        repositoryManager.commit("first commit");
        repositoryManager.checkout("not_master");
    }

    @Test
    public void deleteFilesAndReturnWithCheckout() throws Exception {
        RepositoryManager.initRepository(root);
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        RepositoryManager.getRepositoryManager(root).commit("first commit");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        RepositoryManager.getRepositoryManager(root).checkout("master");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test(expected = BranchAlreadyExistsException.class)
    public void createBranchWithExistingName() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).createBranch("master");
    }

    @Test
    public void createAndRemoveBranch() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).createBranch("second");
        RepositoryManager.getRepositoryManager(root).removeBranch("second");
    }

    @Test
    public void createBranchCheckoutAndAddSomeFiles() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).createBranch("second");
        RepositoryManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        RepositoryManager.getRepositoryManager(root).commit("commit in second branch");
    }

    @Test
    /* Tests that files appear after checkout. */
    public void backAndForthCheckout() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).createBranch("second");
        RepositoryManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        RepositoryManager.getRepositoryManager(root).commit("commit in second branch");
        RepositoryManager.getRepositoryManager(root).checkout("master");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        RepositoryManager.getRepositoryManager(root).checkout("second");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test
     /* Tests that files from another branch appear after merge. */
    public void checkoutAndMergeTest() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).createBranch("second");
        RepositoryManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(RepositoryManager.getRepositoryManager(root));
        RepositoryManager.getRepositoryManager(root).commit("commit in second branch");
        RepositoryManager.getRepositoryManager(root).checkout("master");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        RepositoryManager.getRepositoryManager(root).merge("second");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test
    public void testClean() throws Exception {
        RepositoryManager.initRepository(root);
        RepositoryManager.getRepositoryManager(root).add(root.resolve("file"));
        RepositoryManager.getRepositoryManager(root).clean();
        assertTrue(Files.exists(root.resolve("file")));
        assertFalse(Files.exists(root.resolve("dir").resolve("file")));
    }
}