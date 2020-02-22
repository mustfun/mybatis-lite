package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * @function 报错预警
 */
public abstract class AbstractMapperInspection extends AbstractBaseJavaLocalInspectionTool{

    public static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];


}
