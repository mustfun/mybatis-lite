package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface Choose extends DomElement {

    @NotNull
    @Required
    @SubTagList("when")
    public List<When> getWhens();

    @SubTag("otherwise")
    public Otherwise getOtherwise();

}
