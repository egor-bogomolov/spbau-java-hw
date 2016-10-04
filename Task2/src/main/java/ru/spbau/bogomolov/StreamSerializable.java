package ru.spbau.bogomolov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by egor on 9/21/16.
 */

public interface StreamSerializable {

    void serialize(OutputStream out) throws IOException;

    /**
     * Replace current state with data from input stream
     */
    void deserialize(InputStream in) throws IOException, ClassNotFoundException;
}
