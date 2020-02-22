package com.github.mustfun.mybatis.plugin.locator;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * @function 包定位策略类
 */
public class PackageLocateStrategy extends LocateStrategy {

    private PackageProvider provider = new MapperXmlPackageProvider();

    /**
     * 检查传递进来的class文件是不是对应的mapper文件
     * @param clazz
     * @return
     */
    @Override
    public boolean apply(@NotNull PsiClass clazz) {
        String packageName = ((PsiJavaFile) clazz.getContainingFile()).getPackageName();
        PsiPackage pkg = JavaPsiFacade.getInstance(clazz.getProject()).findPackage(packageName);
        for (PsiPackage tmp : provider.getPackages(clazz.getProject())) {
            if (tmp.equals(pkg)) {
                return true;
            }
        }
        return false;
    }

}
