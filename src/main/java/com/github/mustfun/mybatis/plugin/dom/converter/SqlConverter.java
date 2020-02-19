package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.intellij.util.xml.ConvertContext;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @function mapper的sql标签使用，指向id
 */
public class SqlConverter extends IdBasedTagConverter {

    @NotNull
    @Override
    public Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context) {
        return mapper.getSqls();
    }

}
