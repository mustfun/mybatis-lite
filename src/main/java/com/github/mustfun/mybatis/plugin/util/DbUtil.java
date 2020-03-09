package com.github.mustfun.mybatis.plugin.util;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.classpath.SimpleClasspathElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    public DbUtil(String url, String userName, String password) {
        this.url = url;
        this.username = userName;
        this.password = password;
    }

    public DbUtil() {

    }


    public Connection getConnection(Project project, String cacheKey) {
        Connection connection = ConnectionHolder.getInstance(project).getConnection(cacheKey);
        if (connection != null) {
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
        if (conn != null) {
            ConnectionHolder.getInstance(project).addConnection(cacheKey, conn);
        }
        return conn;
    }

    /**
     * @param project
     * @return
     */
    public Connection getSqlliteConnection(Project project) {
        Connection connection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.SQL_LITE_CONNECTION);
        if (connection != null) {
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
        if (conn != null) {
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

            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            FileOutputStream fileOutputStreamBk = new FileOutputStream(new File(tempFile,"generate_web_bk.db"));
            byte[] buffer = new byte[32768];
            int length;
            while ((length = stream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
                fileOutputStreamBk.write(buffer, 0, length);
            }
            fileOutputStream.close();
            fileOutputStreamBk.close();
            stream.close();

            return targetFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /***
     * 采用自定义驱动方式实现，可以实现多种数据库支持
     * @param project
     * @param cacheKey
     * @param driver
     * @param dbSourcePo
     * @return
     */
    public static Connection getConnectionUseDriver(Project project, String cacheKey, DatabaseDriver driver, DbSourcePo dbSourcePo) {

        Connection connection = ConnectionHolder.getInstance(project).getConnection(cacheKey);
        if (connection != null) {
            return connection;
        }
        SimpleClasspathElement simpleClasspathElement = driver.getAdditionalClasspathElements().get(0);
        Connection conn = null;
        try {
            URL url = new URL(simpleClasspathElement.getClassesRootUrls().get(0).replaceAll("(?:/\\s*)+", "/"));
            ClassLoader classLoader = new ExternalClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
            Class.forName(driver.getDriverClass(), true, classLoader);
            Properties props = new Properties();
            props.setProperty("user", dbSourcePo.getUserName());
            props.setProperty("password", dbSourcePo.getPassword());
            //设置可以获取remarks信息
            props.setProperty("remarks", "true");
            //设置可以获取tables remarks信息
            props.setProperty("useInformationSchema", "true");
            conn = DriverManager.getConnection(dbSourcePo.getDbAddress() + "&serverTimezone=GMT", props);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conn != null) {
            ConnectionHolder.getInstance(project).addConnection(cacheKey, conn);
        }
        return conn;
    }


    /**
     * 获取内部sqlLite数据信息
     */
    public Connection getInnerSqlLiteConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            //String realPath = DbUtil.this.getClass().getResource("/db/generate_web.db").getPath();
            //作为第三方jar包时用resource方式依然访问不到,放弃
            //conn = DriverManager.getConnection("jdbc:sqlite::resource:"+getClass().getResource("/db/generate_web.db").getFile());
            String tempPath = MybatisConstants.TEMP_DIR_PATH + "/db/generate_web_bk.db";
            conn = DriverManager.getConnection("jdbc:sqlite:"+tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
