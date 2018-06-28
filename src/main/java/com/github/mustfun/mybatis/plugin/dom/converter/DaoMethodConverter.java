package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class DaoMethodConverter extends AbstractConverterAdaptor<PsiMethod> {

  @Nullable @Override
  public PsiMethod fromString(@Nullable @NonNls String id, ConvertContext context) {
    Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
    return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id).orNull();
  }

}