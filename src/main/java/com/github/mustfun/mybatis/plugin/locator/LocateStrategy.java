package com.github.mustfun.mybatis.plugin.locator;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @author itar
 * @function 本地策略类
 */
public abstract class LocateStrategy {

    public abstract boolean apply(@NotNull PsiClass clazz);

}
