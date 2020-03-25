package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 */
public interface Package extends DomElement {

    @NotNull
    @Attribute("name")
    public GenericAttributeValue<String> getName();

}
