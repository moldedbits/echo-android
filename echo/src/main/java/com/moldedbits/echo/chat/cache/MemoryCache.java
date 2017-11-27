package com.moldedbits.echo.chat.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;

/**
 * Author viveksingh
 * Date 28/06/17.
 */

public class MemoryCache implements Cache<Bitmap> {
    private static MemoryCache instance;
    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    private final int cacheSize = maxMemory / 8;

    LruCache<String, Bitmap> cache;

    private MemoryCache() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MemoryCache getInstance() {
        if (instance == null) {
            synchronized (MemoryCache.class) {
                if (instance == null) {
                    instance = new MemoryCache();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() throws IOException {
        cache = new LruCache<>(cacheSize);
    }

    @Override
    public Bitmap get(File parent, String name) throws IOException {
        return null;
    }

    @Override
    public Bitmap add(byte[] data, File file) throws IOException {
        return null;
    }

    @Override
    public Bitmap get(String key) throws IOException {
        return cache.get(key);
    }



    @Override
    public void add(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }
}
