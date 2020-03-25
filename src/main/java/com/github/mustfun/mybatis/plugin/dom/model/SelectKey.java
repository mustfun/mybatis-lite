package com.github.mustfun.mybatis.plugin.dom.model;

import com.github.mustfun.mybatis.plugin.dom.converter.AliasConverter;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author itar
 * @function insert里面的比较特殊
 */
public interface SelectKey extends DomElement {

    /**
     *
     * @return
     */
    @NotNull
    @Required
    @Attribute("keyProperty")
    public GenericAttributeValue<String> getKeyProperty();

    /**
     *
     * @return
     */
    @Attribute("order")
    public GenericAttributeValue<String> getOrder();

    /**
     *
     * @return
     */
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    public GenericAttributeValue<String> getResultType();

}
