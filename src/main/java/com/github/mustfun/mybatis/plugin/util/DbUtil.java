package com.github.mustfun.mybatis.plugin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/4/13
 * @since 1.0
 */
public class DbUtil {
    private  String url;
    private  String username;
    private  String password;

    public DbUtil(String url, String dbName, String userName, String password){
        this.url = "jdbc:mysql://"+url+":3306/"+dbName+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false";
        this.username = userName;
        this.password = password;
    }


    public Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Properties props =new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        //设置可以获取remarks信息
        props.setProperty("remarks", "true");
        //设置可以获取tables remarks信息
        props.setProperty("useInformationSchema", "true");
        try {
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
