package com.github.mustfun.mybatis.plugin.util;

/**
 * @author itar
 * @date 2020-03-02
 * 从sql中提炼表名
 */
public class SqlUtil {

    /**
     *
     * @param sql
     * @return
     */
    public static String getTableNameFromSql(String sql){
        String replace = sql.replace("<", "").replace(">", "");
        return replace;
    }
}
