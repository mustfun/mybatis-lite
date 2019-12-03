package com.github.mustfun.mybatis.plugin.util;

import com.intellij.openapi.application.PathManager;
import com.intellij.psi.util.ReferenceSetBase;

/**
 * @author itar
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

}
