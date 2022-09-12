package com.lxb.rpc.cluster.discovery.backup.file;


import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.backup.BackupDatum;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.util.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lxb.rpc.Plugin.SERIALIZATION;


/**
 * 文件备份
 */
public class FileBackup implements Backup {
    /**
     * 目录
     */
    protected File directory;
    /**
     * 备份的数量
     */
    protected int backups;

    protected Map<String, AtomicInteger> counters = new HashMap<>();

    public FileBackup(File directory, int backups) throws IOException {
        this.directory = directory;
        this.backups = backups;
        if (!directory.exists() && !directory.mkdirs() || !directory.isDirectory()) {
            throw new IOException(String.format("directory is not exists. %s", directory));
        } else if (!directory.canRead() || !directory.canWrite()) {
            throw new IOException(String.format("directory is not readable and writable. %s", directory));
        }
        File[] files = directory.listFiles(o -> o.isFile());
        if (files != null && files.length > 0) {
            //按照时间顺序降序排序
            Arrays.sort(files, (o1, o2) -> {
                long time = o1.lastModified() - o2.lastModified();
                if (time > 0) {
                    return -1;
                } else if (time < 0) {
                    return 1;
                }
                return 0;
            });
            for (File file : files) {
                String name = file.getName();
                int pos = name.lastIndexOf('.');
                if (pos > 0) {
                    String ext = name.substring(pos + 1);
                    name = name.substring(0, pos);
                    try {
                        int id = Integer.parseInt(ext);
                        counters.computeIfAbsent(name, o -> new AtomicInteger(id));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

    @Override
    public BackupDatum restore(final String name) throws IOException {
        AtomicInteger counter = counters.get(name);
        if (counter == null) {
            return null;
        }
        File target = new File(directory, name + "." + counter.get());
        try (FileInputStream out = new FileInputStream(target)) {
            return getSerialization().getSerializer().deserialize(out, BackupDatum.class);
        } catch (SerializerException e) {
            throw new IOException(String.format("Error occurs while restoring %s.", name), e);
        }
    }

    @Override
    public void backup(final String name, final BackupDatum datum) throws IOException {
        AtomicInteger counter = counters.computeIfAbsent(name, o -> new AtomicInteger(-1));
        //加锁防止并发
        synchronized (counter) {
            int cursor = 0;
            //备份数大于1
            if (backups > 1) {
                //下一个游标位置
                cursor = counter.incrementAndGet();
                if (cursor >= backups) {
                    //循环，控制文件数量，避免删除
                    counter.set(0);
                    cursor = 0;
                }
            }

            File target = new File(directory, name + "." + cursor);
            File temp = File.createTempFile("cluster", "backup");
            try (FileOutputStream out = new FileOutputStream(temp)) {
                getSerialization().getSerializer().serialize(out, datum);
                out.flush();
                out.close();
                if (!Files.move(temp, target)) {
                    //删除原始文件
                    throw new IOException(String.format("Error occurs while backuping %s. Failed renaming file %s to %s", name, temp, target));
                }
            } catch (SerializerException | IOException e) {
                throw new IOException(String.format("Error occurs while backuping %s.", name), e);
            } finally {
                if (temp.exists()) {
                    temp.delete();
                }
            }
        }
    }

    /**
     * 获取序列化插件
     *
     * @return
     * @throws IOException
     */
    protected Serialization getSerialization() throws IOException {
        Serialization serialization = SERIALIZATION.getOrDefault("json");
        if (serialization == null) {
            throw new IOException("there is not any serialization plugin");
        }
        return serialization;
    }

}
