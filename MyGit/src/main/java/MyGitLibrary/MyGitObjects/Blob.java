package MyGitLibrary.MyGitObjects;

import MyGitLibrary.Exceptions.FileIOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Class that represents a file in hierarchy of VCS.
 */
class Blob implements MyGitObject, Serializable {

    private String fileName;
    private String hash;
    private byte[] content;

    @Override
    public String getType() {
        return BLOB;
    }

    @Override
    public String getHash() {
        return hash;
    }

    String getFileName() {
        return fileName;
    }

    byte[] getContent() {
        return content;
    }

    Blob(@NotNull Path root, @NotNull byte[] content, @NotNull String fileName) throws FileIOException {
        this.content = content;
        this.fileName = fileName;
        updateHash();
        MyGitObject.write(this, root);
    }

    private void updateHash() {
        byte[] array = new byte[content.length + fileName.getBytes().length];
        System.arraycopy(content, 0, array, 0, content.length);
        System.arraycopy(fileName.getBytes(), 0, array, content.length, fileName.getBytes().length);
        hash = DigestUtils.sha1Hex(array);
    }
}
