package com.github.mustfun.mybatis.plugin.provider;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.intellij.openapi.module.Module;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.SpringImplicitBeansProviderBase;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 将类注册进Spring模型
 * @author itar
 * @date 2020-03-02
 */
public class MapperImplicitBeansProvider extends SpringImplicitBeansProviderBase {


    @Override
    protected Collection<CommonSpringBean> getImplicitBeans(@NotNull Module module) {
        List<CommonSpringBean> beans = new ArrayList<>(5);
        Collection<Mapper> mappers = MapperUtils.findMappers(module.getProject());
        for (Mapper mapper : mappers) {
            String stringValue = MapperUtils.getNamespace(mapper);
            String[] split = stringValue.split("\\.");
            String name = split[split.length - 1];
            this.addImplicitLibraryBean(beans, module, stringValue, name);
        }
        return beans;
    }


    @NotNull
    @Override
    public String getProviderName() {
        return "Mybatis Lite";
    }
}
