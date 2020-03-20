package com.github.mustfun.mybatis.plugin.util;


import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author itar
 * @version 1.0
 * @date 2018/4/16
 * @since 1.0
 */
public class ConnectionHolder {


    private ConcurrentHashMap<String, Connection> connectionMap;
    private Map<String, Object> configMap;
    private Map<String, List<LocalTable>> tableCache;

    public ConnectionHolder() {
        connectionMap = new ConcurrentHashMap<>(4);
        configMap = new ConcurrentHashMap<>(4);
        tableCache = new ConcurrentHashMap<>(4);
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

    public Object getConfig(String key) {
        if (configMap==null){
            return null;
        }
        return configMap.get(key);
    }

    /**
     * 返回是否是单module或者多module
     * @param key
     * @return
     */
    public Pair<Boolean,Object> getConfigOrOne(String key) {
        if (configMap==null){
            return Pair.pair(false, null);
        }
        //项目是多module情况下，一般只有一个，这个时候key是无效的
        if (configMap.size()==1){
            return Pair.pair(false,configMap.get(configMap.keySet().iterator().next()));
        }
        return Pair.pair(true,configMap.get(key));
    }

    public void putConfig(String key ,Object value) {
        if (configMap==null){
            configMap = new HashMap<>(1);
        }
        configMap.put(key, value);
    }

    public List<LocalTable> getTableCache(String key) {
        if (tableCache==null){
            return null;
        }
        return tableCache.get(key);
    }

    public void putTableCache(String key ,List<LocalTable> value) {
        if (tableCache==null){
            tableCache = new HashMap<>(1);
        }
        tableCache.put(key, value);
    }
}
