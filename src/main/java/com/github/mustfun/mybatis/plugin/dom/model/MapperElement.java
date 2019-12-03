package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;

/**
 * <code>MapperElement</code>
 * 代表xml中mapper元素，
 *
 * @author itar
 * @see
 * @since 2018/8/15 v1.0
 */
public interface MapperElement extends DomElement {

    /**
     * namespace属性
     */
    @Required
    @NameValue
    @Attribute("namespace")
    GenericAttributeValue<String> getNameSpace();

    /**
     * 设置值
     */
    void setValue(String content);
}
