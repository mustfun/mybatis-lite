package com.github.mustfun.mybatis.plugin.util;

import com.intellij.openapi.application.PathManager;
import com.intellij.psi.util.ReferenceSetBase;

/**
 * @author yanglin
 */
public final class MybatisConstants {

  public static final String TEMP_DIR_PATH = PathManager.getPluginsPath() + "/temp";

  private MybatisConstants() {
    throw new UnsupportedOperationException();
  }

  public static final String DOT_SEPARATOR = String.valueOf(ReferenceSetBase.DOT_SEPARATOR);

  public static final double PRIORITY = 400.0;

}
