package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class GenerateStatementChooser extends JavaFileIntentionChooser{

  public static final JavaFileIntentionChooser INSTANCE = new GenerateStatementChooser();

  @Override
  public boolean isAvailable(@NotNull PsiElement element) {
    if (!isPositionOfMethodDeclaration(element)) {
      return false;
    }
    PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
    return null != method &&  null != clazz &&
           !JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES) &&
           !isTargetPresentInXml(method) &&
           isTargetPresentInXml(clazz);
  }
}