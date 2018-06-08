package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class UpdateGenerator extends StatementGenerator{

  public UpdateGenerator(@NotNull String... patterns) {
    super(patterns);
  }

  @NotNull @Override
  protected GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
    return mapper.addUpdate();
  }

  @NotNull @Override
  public String getId() {
    return "UpdateGenerator";
  }

  @NotNull @Override
  public String getDisplayText() {
    return "Update Statement";
  }

}
