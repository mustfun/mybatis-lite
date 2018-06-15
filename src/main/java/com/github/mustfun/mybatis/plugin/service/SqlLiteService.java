package com.github.mustfun.mybatis.plugin.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class SqlLiteService {
    private Connection connection;
    private Statement statement;

    public static SqlLiteService getInstance(Connection connection) {
        return new SqlLiteService(connection);
    }

    public SqlLiteService(Connection connection){
        this.connection=connection;
        try {
            this.statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void queryTemplate(Integer id){
        try {
            String sql = "select\n" +
                    "        id, tep_name, tep_desc,create_by,tep_content,vm_type,db_type,create_time\n" +
                    "        from template\n" +
                    "        where id = "+id;
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                System.out.println("id=>" + rs.getInt("id") + ", tep_name=>" + rs.getString("tep_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTemplate(){
        try {
            statement.executeUpdate("DROP TABLE IF EXISTS person");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
