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
 * select有resultMap标签和resultType标签
 */
public interface Select extends GroupTwo, ResultMapGroup {

    /**
     * 事实上如果是简单的PSIClass也可以不需要转换器，可以注释掉那一行
     * @return
     */
    @NotNull
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<PsiClass> getResultType();


}
