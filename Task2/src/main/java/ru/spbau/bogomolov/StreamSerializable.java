package ru.spbau.bogomolov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamSerializable {

    void serialize(OutputStream out) throws IOException;

    void deserialize(InputStream in) throws IOException, ClassNotFoundException;
}