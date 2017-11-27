package com.moldedbits.echo.chat.cache;

import com.moldedbits.echo.chat.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author viveksingh
 * Date 28/06/17.
 * This class add the file to the disk and cache it for faster use. It uses DiskLruCache form google
 * for caching files
 */

public final class DiskCache implements Cache<File> {
    private final Object mDiskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private DiskLruCache mDiskLruCache;
    private File cacheDirectory;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100MB


    @Override
    public void init() throws IOException {
        if (this.cacheDirectory == null) {
            throw new NullPointerException("Please set the cache directory first");
        }
        mDiskLruCache = DiskLruCache.open(this.cacheDirectory, 1, 1, DISK_CACHE_SIZE);
    }

    /**
     * Set the cache directory i.e. where to store files
     * @param cacheDirectory directory
     */
    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the file form cache. FYI File's absolute path serves as key
     * @param parent cache directory
     * @param name file name
     * @return cached file
     * @throws IOException
     */
    @Override
    public File get(File parent, String name) throws IOException {
        File file = new File(parent, name);
        if (file.exists()) {
            return file;
        } else {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(file.getAbsolutePath());

            InputStream stream = null;
            if (snapshot != null) {
                stream = snapshot.getInputStream(0);
                return FileUtil.inputStreamToFile(stream, file.getParentFile(), file.getName());
            }

        }
        return null;
    }

    @Override
    public final File get(String key) throws IOException {
        // TODO: 08/10/17 provide definition if needed later
        return null;
    }

    /**
     * Adding data to cache
     * @param data
     * @param absoluteFile
     * @return
     * @throws IOException
     */
    @Override
    public File add(byte[] data, File absoluteFile) throws IOException {
        // write the byte array to file and
        File file = FileUtil.toFile(data, absoluteFile.getParentFile(), absoluteFile.getName());
        // making a disk cache entry
        DiskLruCache.Editor editor = mDiskLruCache.edit(file.getAbsolutePath());
        if (editor != null) {
            mDiskLruCache.edit(file.getAbsolutePath()).newOutputStream(0).write(data);
        }
        return file;
    }

    @Override
    public final void add(String key, File bitmap) {

    }
}