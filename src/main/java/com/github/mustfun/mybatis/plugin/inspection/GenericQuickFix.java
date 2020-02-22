package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * @function 通用快速修复
 */
public abstract class GenericQuickFix implements LocalQuickFix {

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

}
