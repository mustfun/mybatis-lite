package com.github.mustfun.mybatis.plugin.util;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private String url;
    private String username;
    private String password;

    public DbUtil(String url, String dbName, String userName, String password) {
        this.url = "jdbc:mysql://" + url + ":3306/" + dbName
            + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT";
        this.username = userName;
        this.password = password;
    }

    /**
     * 增加端口配置
     *
     * @date 2019/1/22
     */
    public DbUtil(String url, String dbName, String userName, String password, int port) {
        this.url = "jdbc:mysql://" + url + ":" + port + "/" + dbName
            + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT";
        this.username = userName;
        this.password = password;
    }

    public DbUtil() {

    }


    public Connection getConnection(Project project) {
        Connection connection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.MYSQL_DB_CONNECTION);
        if (connection!=null){
            return connection;
        }
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
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
        if (conn!=null) {
            ConnectionHolder.getInstance(project).addConnection(MybatisConstants.MYSQL_DB_CONNECTION, conn);
        }
        return conn;
    }

    /**
     *
     * @return
     * @param project
     */
    public Connection getSqlliteConnection(Project project) {
        Connection connection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.SQL_LITE_CONNECTION);
        if (connection!=null){
            return connection;
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            //String realPath = PathManager.getPluginsPath();
            // File path = PluginManager.getPlugin(PluginId.getId("mybatis-plugin-free")).getPath();
            //查看是否存在db文件，不存在就创建，存在就直接连
            String tempPath = MybatisConstants.TEMP_DIR_PATH + "/db/generate_web.db";
            File tempPathFile = new File(tempPath);
            if (!tempPathFile.exists()) {
                InputStream s = DbUtil.this.getClass().getResourceAsStream("/db/generate_web.db");
                tempPath = createCustomFontUrl(s);
            }
            conn = DriverManager.getConnection("jdbc:sqlite:" + tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conn!=null) {
            ConnectionHolder.getInstance(project).addConnection(MybatisConstants.SQL_LITE_CONNECTION, conn);
        }
        return conn;
    }

    /**
     * 将里面的数据库文件转移出来,非常巧妙,非常非常,终于解决难点OK
     */
    protected String createCustomFontUrl(InputStream stream) {
        String tempDirPath = MybatisConstants.TEMP_DIR_PATH;
        try {
            File tempFile = new File(tempDirPath + "/db");
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File targetFile = new File(tempFile, "generate_web.db");
            System.out.println("creating temp db File file: " + targetFile.getAbsolutePath());

            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[32768];
            int length;
            while ((length = stream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            stream.close();

            return targetFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
