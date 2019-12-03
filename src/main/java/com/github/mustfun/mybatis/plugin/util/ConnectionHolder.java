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

    private static ConcurrentHashMap<String, Connection> connectionMap = new ConcurrentHashMap<>(4);


    public static void addConnection(String key, Connection connection) {
        String digest = DigestUtils.md5Hex(key.getBytes());
        connectionMap.put(digest, connection);
    }

    public static Connection getConnection(String key) {
        return connectionMap.get(DigestUtils.md5Hex(key.getBytes()));
    }

    public static void remove() {
        connectionMap.clear();
    }

}
