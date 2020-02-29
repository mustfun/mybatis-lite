package com.github.mustfun.mybatis.plugin.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author itar
 * @date 2020-02-24
 * 获取dbService服务的工厂类
 */
public class DbServiceFactory {

    private Project project;

    public DbServiceFactory(Project project) {
        this.project = project;
    }

    /**
     * 获取Service实例
     * @param project
     * @return
     */
    public static DbServiceFactory getInstance(@NotNull Project project){
        return ServiceManager.getService(project,DbServiceFactory.class);
    }

    /**
     * 获取mysql的service
     * @return
     */
    public MysqlService createMysqlService(){
        return new MysqlService(project);
    }

    /**
     * 获取SqlLite的service
     * @return
     */
    public SqlLiteService createSqlLiteService(){
        return new SqlLiteService(project);
    }
}
