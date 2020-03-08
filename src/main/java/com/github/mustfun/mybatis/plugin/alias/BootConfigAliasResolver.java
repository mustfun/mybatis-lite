package com.github.mustfun.mybatis.plugin.alias;

import com.github.mustfun.mybatis.plugin.model.ModuleConfig;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * @author itar
 * @date 2020-03-08
 * @funcion bean别名解析器
 */
public class BootConfigAliasResolver extends PackageAliasResolver {


    public BootConfigAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        Set<String> res = Sets.newHashSet();
        //如果没有config是boot文件的
        Module currentModule = ModuleUtil.findModuleForPsiElement(element);
        if(currentModule==null){
            return res;
        }
        Pair<Boolean, Object> config = ConnectionHolder.getInstance(project).getConfigOrOne(currentModule.getName()) ;
        if (config.second==null){
            return res;
        }
        ModuleConfig moduleConfig = (ModuleConfig) config.second;
        String typeAliasPackage = moduleConfig.getTypeAliasPackage();
        //找到package
        //PsiDirectory possiblePackageDirectoryInModule = PackageUtil.findPossiblePackageDirectoryInModule(currentModule, typeAliasPackage);
        res.add(typeAliasPackage);
        return res;
    }

    private Module getPoModule() {
        return null;
    }


}
