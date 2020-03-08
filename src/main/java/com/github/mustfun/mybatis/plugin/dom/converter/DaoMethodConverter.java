package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author yanglin
 * @update itar dao层method 转变对象
 * 解析成psiMethod / psiField / PsiClass对象等都能够自动跳转，就是这么神奇
 */
public class DaoMethodConverter extends AbstractConverterAdaptor<PsiMethod> {

    @Nullable
    @Override
    public PsiMethod fromString(@Nullable @NonNls String id, ConvertContext context) {
        //获取唤醒的当前元素的mapper对象
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        //找到当前mapper的方法
        return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id).orElse(null);
    }

    @Override
    public String getErrorMessage(@Nullable String s, ConvertContext context) {
        if (!StringUtils.isEmpty(s)){
            return "没有在Dao接口中找到"+s+"方法";
        }
        return super.getErrorMessage(s, context);
    }

    @NotNull
    @Override
    public Collection<? extends PsiMethod> getVariants(ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        return JavaUtils.findMethods(context.getProject(), MapperUtils.getNamespace(mapper));
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(PsiMethod psiMethod) {
        return LookupElementBuilder.createWithIcon(psiMethod).withTypeText(((PsiClassImpl) psiMethod.getParent()).getName());
    }
}