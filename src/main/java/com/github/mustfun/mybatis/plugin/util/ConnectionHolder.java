package com.github.mustfun.mybatis.plugin.util;


import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/4/16
 * @since 1.0
 */
public class ConnectionHolder {

    private static ThreadLocal<ConcurrentHashMap<String, Connection>> connectionMapThreadLocal = new ThreadLocal<>();


    public static void addConnection(String key, Connection connection) {
        String digest = DigestUtils.md5Hex(key.getBytes());
        ConcurrentHashMap<String, Connection> stringConnectionConcurrentHashMap = connectionMapThreadLocal.get();
        if (stringConnectionConcurrentHashMap==null){
            stringConnectionConcurrentHashMap = new ConcurrentHashMap<>(4);
        }
        stringConnectionConcurrentHashMap.put(digest, connection);
        connectionMapThreadLocal.set(stringConnectionConcurrentHashMap);
    }

    public static Connection getConnection(String key) {
        if (connectionMapThreadLocal.get()==null){
            return  null;
        }
        return connectionMapThreadLocal.get().get(DigestUtils.md5Hex(key.getBytes()));
    }

    public static void remove() {
        connectionMapThreadLocal.remove();
    }

}
