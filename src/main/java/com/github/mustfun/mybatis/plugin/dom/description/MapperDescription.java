package com.github.mustfun.mybatis.plugin.dom.description;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.DomUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class MapperDescription extends DomFileDescription<Mapper> {

  public MapperDescription() {
    super(Mapper.class, "mapper");
  }

  @Override
  public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
    return DomUtils.isMybatisFile(file);
  }

}
