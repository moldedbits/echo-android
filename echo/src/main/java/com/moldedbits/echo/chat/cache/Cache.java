package com.moldedbits.echo.chat.cache;

import java.io.File;
import java.io.IOException;

/**
 * Author viveksingh
 * Date 28/06/17.
 */

public interface Cache<T> {
    void init() throws IOException;

    T get(File parent, String name) throws IOException;

    T get(String key) throws IOException;

    T add(byte[] data, File file) throws IOException;

    void add(String key, T bitmap);
}
