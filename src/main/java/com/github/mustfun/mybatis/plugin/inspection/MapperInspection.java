package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;

/**
 * @author yanglin
 */
public abstract class MapperInspection extends BaseJavaLocalInspectionTool {

  public static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];

}
