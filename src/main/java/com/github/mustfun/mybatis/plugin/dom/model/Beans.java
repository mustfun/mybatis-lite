package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 */
public interface Beans extends DomElement {

  @NotNull
  @SubTagList("bean")
  public List<Bean> getBeans();

}
