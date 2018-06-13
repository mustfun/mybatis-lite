package com.github.mustfun.mybatis.plugin.service;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.util.DbUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class DbService {

    public static DbService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, DbService.class);
    }

    public  boolean getConnection(DbSourcePo configPo) {
        DbUtil dbUtil = new DbUtil(configPo.getDbAddress(), configPo.getDbName(), configPo.getUserName(), configPo.getPassword());
        Connection connection = dbUtil.getConnection();
        if (connection == null) {
            return false;
        }
        return true;
    }
}
