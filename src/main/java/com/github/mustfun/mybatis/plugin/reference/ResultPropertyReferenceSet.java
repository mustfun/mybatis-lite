package com.github.mustfun.mybatis.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @function 要想构造ReferenceSet必须继承ReferenceSetBase
 */
public class ResultPropertyReferenceSet extends ReferenceSetBase<PsiReference> {

    public ResultPropertyReferenceSet(String text, @NotNull PsiElement element, int offset) {
        //最后一个是分隔符
        super(text, element, offset, DOT_SEPARATOR);
    }

    @Nullable
    @NonNls
    @Override
    protected PsiReference createReference(TextRange range, int index) {
        XmlAttributeValue element = (XmlAttributeValue) getElement();
        return null == element ? null : new ContextPsiFieldReference(element, range, index);
    }

}