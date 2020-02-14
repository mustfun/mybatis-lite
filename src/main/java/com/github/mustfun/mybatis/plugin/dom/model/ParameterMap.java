package com.github.mustfun.mybatis.plugin.dom.model;

import com.github.mustfun.mybatis.plugin.dom.converter.AliasConverter;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @function parameterMap标签
 */
public interface ParameterMap extends IdDomElement {

    /**
     *
     * @return
     */
    @NotNull
    @Attribute("type")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<PsiClass> getType();

    /**
     *
     * @return
     */
    @SubTagList("parameter")
    public List<Parameter> getParameters();

}
