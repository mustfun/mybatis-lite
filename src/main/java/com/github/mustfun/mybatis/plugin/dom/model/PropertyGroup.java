package com.github.mustfun.mybatis.plugin.dom.model;

import com.github.mustfun.mybatis.plugin.dom.converter.PropertyConverter;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author yanglin
 */
public interface PropertyGroup extends DomElement {

  @Attribute("property")
  @Convert(PropertyConverter.class)
  GenericAttributeValue<XmlAttributeValue> getProperty();
}
