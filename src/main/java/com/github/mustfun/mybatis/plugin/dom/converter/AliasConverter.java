package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.alias.AliasClassReference;
import com.github.mustfun.mybatis.plugin.alias.AliasFacade;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomJavaUtil;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @function String转PsiClass的一个Converter，事实上系统内置PsiClassConverter ，这里是别名使用，比如list就要解析成java.util.list
 * 为返回的GenericDomValue创建PsiReferences时调用，
 * PsiReferences应该是soft (软引用)(PsiReference.isSoft()应该返回true)。要突出显示未解析的引用，请创建com.intellij.util.xml.highlight.DomElementsInspection并注册上去
 */
public class AliasConverter extends AbstractConverterAdaptor<PsiClass> implements CustomReferenceConverter<PsiClass> {

    private PsiClassConverter delegate = new PsiClassConverter();

    /**
     * 从string返回PSIClass
     * @param s
     * @param context
     * @return
     */
    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String s, ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) {
            return null;
        }
        if (!s.contains(MybatisConstants.DOT_SEPARATOR)) {
            return AliasFacade.getInstance(context.getProject()).findPsiClass(context.getXmlElement(), s).orNull();
        }
        return DomJavaUtil.findClass(s.trim(), context.getFile(), context.getModule(),
            GlobalSearchScope.allScope(context.getProject()));
    }

    /**
     * 从PSIClass返回到string
     * @param psiClass
     * @param context
     * @return
     */
    @Nullable
    @Override
    public String toString(@Nullable PsiClass psiClass, ConvertContext context) {
        return delegate.toString(psiClass, context);
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element,
        ConvertContext context) {
        if (((XmlAttributeValue) element).getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            return delegate.createReferences(value, element, context);
        } else {
            return new PsiReference[]{new AliasClassReference((XmlAttributeValue) element)};
        }
    }
}
