package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface Beans extends DomElement {

    @NotNull
    @SubTagList("bean")
    public List<Bean> getBeans();

}
