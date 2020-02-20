package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.reference.ResultPropertyReferenceSet;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @function <result propertity=''></result> 等使用的需要可以跳转到java的属性类
 */
public class PropertyConverter extends AbstractConverterAdaptor<XmlAttributeValue> implements
    CustomReferenceConverter<XmlAttributeValue> {

    /**
     * 跟java实体类做一个连接，可以跳转
     * @param value
     * @param element
     * @param context
     * @return
     * @see com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider#getReferencesByElement(PsiElement) 
     */
    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element,
        ConvertContext context) {
        final String s = value.getStringValue();
        if (s == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        //这个类更加底层一点，确定元素的位置，进行跳转
        return new ResultPropertyReferenceSet(s, element, ElementManipulators.getOffsetInElement(element))
            .getPsiReferences();
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String s, ConvertContext context) {
        DomElement ctxElement = context.getInvocationElement();
        return ctxElement instanceof GenericAttributeValue ? ((GenericAttributeValue) ctxElement).getXmlAttributeValue()
            : null;
    }

}
