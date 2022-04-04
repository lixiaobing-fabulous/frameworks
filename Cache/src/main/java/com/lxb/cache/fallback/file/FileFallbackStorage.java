package com.lxb.cache.fallback.file;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.lxb.cache.cache.Cache.Entry;
import com.lxb.cache.fallback.BaseCacheFallbackStorage;


/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class FileFallbackStorage extends BaseCacheFallbackStorage<Object, Object> {
    private static final File CACHE_FALLBACK_DIRECTORY = new File(".cache/fallback/");

    public FileFallbackStorage() {
        super(Integer.MAX_VALUE);
        makeCacheFallbackDirectory();
    }

    File toStorageFile(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }

    @Override
    public void destroy() {
        destroyCacheFallbackDirectory();
    }

    @Override
    public Object load(Object key) {
        File storageFile = toStorageFile(key);
        if (!storageFile.exists() || !storageFile.canRead()) {
            return null;
        }
        Object value = null;
        try (FileInputStream inputStream = new FileInputStream(storageFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            value = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return value;
    }

    @Override
    public void write(Entry<?, ?> entry) {
        Object key = entry.getKey();
        Object value = entry.getValue();
        File storageFile = toStorageFile(key);
        if (storageFile.exists() && !storageFile.canWrite()) {
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(storageFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        ) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
        }

    }

    @Override
    public void delete(Object key) {
        File storageFile = toStorageFile(key);
        storageFile.delete();

    }

    private void destroyCacheFallbackDirectory() {
        if (CACHE_FALLBACK_DIRECTORY.exists()) {
            for (File storageFile : CACHE_FALLBACK_DIRECTORY.listFiles()) {
                storageFile.delete();
            }
        }
    }

    private void makeCacheFallbackDirectory() {
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new RuntimeException(format("The fallback directory[path:%s] can't be created!"));
        }
    }

}
