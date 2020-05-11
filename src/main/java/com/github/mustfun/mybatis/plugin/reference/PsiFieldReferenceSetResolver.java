package com.github.mustfun.mybatis.plugin.reference;

import com.github.mustfun.mybatis.plugin.dom.MapperBacktrackingUtils;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author yanglin
 * @updater itar
 * @function PSI属性的一个跳转处理类
 */
public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {

    protected PsiFieldReferenceSetResolver(XmlAttributeValue from) {
        super(from);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }

    @NotNull
    @Override
    public Optional<PsiField> resolve(@NotNull PsiField current, @NotNull String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<PsiField> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(getElement());
        return clazz.flatMap(psiClass -> JavaUtils.findPsiField(psiClass, firstText));
    }

}