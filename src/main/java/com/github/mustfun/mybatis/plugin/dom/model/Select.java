package com.github.mustfun.mybatis.plugin.dom.model;

import com.github.mustfun.mybatis.plugin.dom.converter.AliasConverter;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 */
public interface Select extends GroupTwo, ResultMapGroup {

    @NotNull
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<PsiClass> getResultType();


}
