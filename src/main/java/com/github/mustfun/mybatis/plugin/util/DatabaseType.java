package com.github.mustfun.mybatis.plugin.util;

/**
 * 将来支持多数据库用
 * @author itar
 *
 */
public enum DatabaseType {

    /**
     *
     */
    MySQL("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s", "mysql-connector-java-5.1.37.jar"),
    /**
     *
     */
    MySQL_8("com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useUnicode=true&useSSL=false&characterEncoding=%s", "mysql-connector-java-8.0.12.jar"),
    /**
     *
     */
    Oracle("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s:%s", "ojdbc14.jar"),
    /**
     *
     */
    PostgreSQL("org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s", "postgresql-9.4.1209.jar"),
    /**
     *
     */
    SqlServer("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%s;databaseName=%s", "sqljdbc4-4.0.jar"),
    /**
     *
     */
    Sqlite("org.sqlite.JDBC", "jdbc:sqlite:%s", "sqlite-jdbc-3.19.3.jar"),
    /**
     *
     */
    MariaDB("org.mariadb.jdbc.Driver", "", "mariadb-java-client-2.3.0.jar");


    private String driverClassName;
    private String urlPattern;
    private String driverName;

    DatabaseType(String driverClassName, String urlPattern, String driverName) {
        this.driverClassName = driverClassName;
        this.urlPattern = urlPattern;
        this.driverName = driverName;
    }
}