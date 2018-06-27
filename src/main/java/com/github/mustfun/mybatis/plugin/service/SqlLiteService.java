package com.github.mustfun.mybatis.plugin.service;

import com.github.mustfun.mybatis.plugin.model.PluginConfig;
import com.github.mustfun.mybatis.plugin.model.Template;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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


    public Template queryTemplateById(Integer id){
        try {
            String sql = "select\n" +
                    "        id, tep_name, tep_desc,create_by,tep_content,vm_type,db_type,create_time\n" +
                    "        from template\n" +
                    "        where id = "+id;
            ResultSet rs = statement.executeQuery(sql);
            Template template = new Template();
            while (rs.next()) {
                template.setId(rs.getInt("id"));
                template.setTepName(rs.getString("tep_name"));
                template.setTepDesc(rs.getString("tep_desc"));
                template.setTepContent(rs.getString("tep_content"));
                template.setDbType(rs.getInt("db_type"));
                template.setVmType(rs.getInt("vm_type"));
            }
            return template;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTemplate(Integer id){
        try {
            statement.executeUpdate("delete from template where id="+id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTemplate(Template template){
        try {
            statement.executeUpdate("update template set tep_content='"+template.getTepContent()+"' where id="+template.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Template> queryTemplateList() {
        try {
            String sql = "select\n" +
                    "        id, tep_name, tep_desc,create_by,tep_content,vm_type,db_type,create_time\n" +
                    "        from template";
            ResultSet rs = statement.executeQuery(sql);
            List<Template> list = new ArrayList<>();
            while (rs.next()) {
                Template template = new Template();
                template.setId(rs.getInt("id"));
                template.setTepName(rs.getString("tep_name"));
                template.setTepDesc(rs.getString("tep_desc"));
                template.setTepContent(rs.getString("tep_content"));
                template.setDbType(rs.getInt("db_type"));
                template.setCreateBy(rs.getString("create_by"));
                template.setVmType(rs.getInt("vm_type"));
                list.add(template);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<PluginConfig> queryPluginConfigList(){
        try {
            String sql = "select\n" +
                    "        id,key,value\n" +
                    "        from plugin_config";
            List<PluginConfig> list = new ArrayList<>();

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                PluginConfig template = new PluginConfig();
                template.setId(rs.getInt("id"));
                template.setKey(rs.getString("key"));
                template.setValue(rs.getString("value"));
                list.add(template);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PluginConfig queryPluginConfigByKey(String key){
        try {
            String sql = "select\n" +
                    "        id,key,value\n" +
                    "        from plugin_config where key='"+key+"'limit 1";

            ResultSet rs = statement.executeQuery(sql);
            PluginConfig template = new PluginConfig();
            while (rs.next()) {
                template.setId(rs.getInt("id"));
                template.setKey(rs.getString("key"));
                template.setValue(rs.getString("value"));
            }
            return template;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
