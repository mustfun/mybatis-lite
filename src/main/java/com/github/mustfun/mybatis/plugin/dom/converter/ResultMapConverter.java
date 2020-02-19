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
 * @updater itar
 * @function resultMap解析器，如果发现了extends属性就会调用这个converterl
 */
public class ResultMapConverter extends IdBasedTagConverter {

    /**
     * 上层getValue调用  - string转为xmlValue
     * @param mapper mapper in the project, null if {@link #IdBasedTagConverter} is false
     * @param context the dom convert context
     * @return
     * @function 如果唤醒的上下文是<resultMap extends='BaseResultMap'></resultMap>引起的转换
     *           那么toString的时候就将整个 resultMap给它
     *           如果唤醒的是<select id='BaseResultMap'></select>，那么就获取所有的resultMap然后filter
     */
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
