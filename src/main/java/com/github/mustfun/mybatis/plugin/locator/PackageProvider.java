package com.github.mustfun.mybatis.plugin.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * @function 包的提供者
 */
public abstract class PackageProvider {

    @NotNull
    public abstract Set<PsiPackage> getPackages(@NotNull Project project);

}