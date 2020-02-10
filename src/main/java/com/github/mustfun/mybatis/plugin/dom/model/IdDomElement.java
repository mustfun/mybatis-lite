package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;

/**
 * @author yanglin
 * @update itar
 * @function 通用的dom元素 -  id
 */
public interface IdDomElement extends DomElement {

    /**
     * 通用的属性值
     */
    @Required
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

    void setValue(String content);
}
