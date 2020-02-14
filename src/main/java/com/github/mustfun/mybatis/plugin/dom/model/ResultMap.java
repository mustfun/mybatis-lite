package com.github.mustfun.mybatis.plugin.dom.model;

import com.github.mustfun.mybatis.plugin.dom.converter.AliasConverter;
import com.github.mustfun.mybatis.plugin.dom.converter.ResultMapConverter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author yanglin
 * @function resultMap的一些属性
 */
public interface ResultMap extends GroupFour, IdDomElement {

    /**
     * extend属性
     * @return
     */
    @NotNull
    @Attribute("extends")
    @Convert(ResultMapConverter.class)
    public GenericAttributeValue<XmlAttributeValue> getExtends();

    /**
     * type属性
     * @return
     */
    @NotNull
    @Attribute("type")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<PsiClass> getType();

}