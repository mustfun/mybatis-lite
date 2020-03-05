package com.github.mustfun.mybatis.plugin.util;

import com.intellij.openapi.application.PathManager;
import com.intellij.psi.util.ReferenceSetBase;

import java.util.Arrays;

/**
 * @author itar
 * @function 常量配置
 */
public final class MybatisConstants {

    public static final String PLUGIN_NAME = "Mybatis-Lite";

    public static final String TEMP_DIR_PATH = PathManager.getPluginsPath() + "/" + PLUGIN_NAME + "/temp";

    public static final String SQL_LITE_CONNECTION = "sqlLiteConnection";
    public static final String MYSQL_DB_CONNECTION = "mysqlDbConnection";

    private MybatisConstants() {
        throw new UnsupportedOperationException();
    }

    public static final String DOT_SEPARATOR = String.valueOf(ReferenceSetBase.DOT_SEPARATOR);

    public static final double PRIORITY = 400.0;

    public static final String[] DEFAULT_SELECT_PATTEN = Arrays.asList("select", "get", "look", "find",
            "list", "search", "count", "query").toArray(new String[0]);
    public static final String[] DEFAULT_UPDATE_PATTEN = Arrays.asList("update", "modify", "set").toArray(new String[0]);
    public static final String[] DEFAULT_INSERT_PATTEN = Arrays.asList("insert", "add", "new", "batch", "batchInsert","insertBatch").toArray(new String[0]);
    public static final String[] DEFAULT_DELETE_PATTEN = Arrays.asList("del", "cancel", "delete", "drop").toArray(new String[0]);
    public static final String DEFAULT_SELECT_PATTEN_KEY = "DEFAULT_SELECT_PATTEN";
    public static final String DEFAULT_UPDATE_PATTEN_KEY = "DEFAULT_UPDATE_PATTEN";
    public static final String DEFAULT_INSERT_PATTEN_KEY = "DEFAULT_INSERT_PATTEN";
    public static final String DEFAULT_DELETE_PATTEN_KEY = "DEFAULT_DELETE_PATTEN";
    public static final String NAVIGATION_OPEN_STATUS = "NAVIGATION_OPEN_STATUS";
    public static final String TRUE = "1";
    public static final String MODULE_DB_CONFIG = "MODULE_ALL_DB_CONFIG";
}
