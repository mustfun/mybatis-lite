package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.*;

/**
 * @author yanglin
 * @update itar
 * @function 通用的dom元素 -  id select/update等都有id属性
 */
public interface IdDomElement extends DomElement {

    /**
     * 通用的属性值
     * NameValue表示这里的id有可能是string，有可能是其他值
     */
    @Required
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

    /**
     * 固定格式setValue方法，否则加@TagValue注解
     * @param content
     */
    void setValue(String content);


    /**
     * 固定格式getValue，否则加@TagValue注解
     */
    String getValue();
}
