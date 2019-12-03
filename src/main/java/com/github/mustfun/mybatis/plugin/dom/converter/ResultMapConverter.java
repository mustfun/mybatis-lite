package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.ResultMap;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class ResultMapConverter extends IdBasedTagConverter {

    @NotNull
    @Override
    public Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        if (isContextElementOfResultMap(mapper, invocationElement)) {
            return doFilterResultMapItself(mapper, (ResultMap) invocationElement.getParent());
        } else {
            return mapper.getResultMaps();
        }
    }

    private boolean isContextElementOfResultMap(Mapper mapper, DomElement invocationElement) {
        return MapperUtils.isMapperWithSameNamespace(MapperUtils.getMapper(invocationElement), mapper)
            && invocationElement.getParent() instanceof ResultMap;
    }

    private Collection<? extends IdDomElement> doFilterResultMapItself(Mapper mapper, final ResultMap resultMap) {
        return Collections2.filter(mapper.getResultMaps(), new Predicate<ResultMap>() {
            @Override
            public boolean apply(ResultMap input) {
                return !MapperUtils.getId(input).equals(MapperUtils.getId(resultMap));
            }
        });
    }

}
