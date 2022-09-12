package com.lxb.rpc.cluster.discovery.backup;



import com.lxb.extension.Extensible;

import java.io.IOException;

/**
 * 备份恢复
 */
@Extensible("backup")
public interface Backup {

    /**
     * 恢复备份数据
     *
     * @param name 名称
     * @return 备份数据
     * @throws IOException io异常
     */
    BackupDatum restore(String name) throws IOException;

    /**
     * 备份数据
     *
     * @param name  名称
     * @param datum 备份数据
     * @throws IOException io异常
     */
    void backup(String name, BackupDatum datum) throws IOException;

}
