package com.github.mustfun.mybatis.plugin.util;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/4/16
 * @since 1.0
 */
public class ConnectionHolder {


    private ConcurrentHashMap<String, Connection> connectionMap;

    public ConnectionHolder() {
        connectionMap = new ConcurrentHashMap<>(4);
    }

    public static ConnectionHolder getInstance(@NotNull Project project){
        return ServiceManager.getService(project, ConnectionHolder.class);
    }

    public void addConnection(String key, Connection connection) {
        String digest = DigestUtils.md5Hex(key.getBytes());
        if (connectionMap == null) {
            connectionMap = new ConcurrentHashMap<>(4);
        }
        connectionMap.put(digest, connection);
    }

    public Connection getConnection(String key) {
        if (connectionMap == null) {
            return null;
        }
        return connectionMap.get(DigestUtils.md5Hex(key.getBytes()));
    }

    public void remove() {
        connectionMap.clear();
    }

}
