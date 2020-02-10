package com.github.mustfun.mybatis.plugin.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public abstract class PackageProvider {

    @NotNull
    public abstract Set<PsiPackage> getPackages(@NotNull Project project);

}