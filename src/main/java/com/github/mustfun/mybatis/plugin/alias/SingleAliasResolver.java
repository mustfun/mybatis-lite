package com.github.mustfun.mybatis.plugin.alias;

import com.github.mustfun.mybatis.plugin.dom.model.TypeAlias;
import com.github.mustfun.mybatis.plugin.model.ModuleConfig;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.google.common.collect.Sets;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Processor;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @funcion 单个别名解析器 - mybatis配置文件用
 * 1. <typealiaes><typealias alias='' type=''></typealias></typealiaes>
 * 2. <typealiaes><package name="'/></typealiaes>
 * 3. 注解 @alias
 * 4. boot yml typealiaes package
 */
public class SingleAliasResolver extends AliasResolver {

    public SingleAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        final Set<AliasDesc> result = Sets.newHashSet();
        MapperUtils.processConfiguredTypeAliases(project, new Processor<TypeAlias>() {
            @Override
            public boolean process(TypeAlias typeAlias) {
                //所有的typeAlias都放在里面了
                addAliasDesc(result, typeAlias.getType().getValue(), typeAlias.getAlias().getStringValue());
                return true;
            }
        });
        //如果没有config是boot文件的
        Module currentModule = ModuleUtil.findModuleForPsiElement(element);
        if(currentModule==null){
            return result;
        }
        ModuleConfig config = (ModuleConfig)ConnectionHolder.getInstance(project).getConfigOrOne(currentModule.getName()) ;
        if (config==null){
            return result;
        }
        String typeAliasPackage = config.getTypeAliasPackage();
        //找到package
        PsiDirectory possiblePackageDirectoryInModule = PackageUtil.findPossiblePackageDirectoryInModule(currentModule, typeAliasPackage);
        if (possiblePackageDirectoryInModule==null){
            return result;
        }
        PsiFile[] files = possiblePackageDirectoryInModule.getFiles();
        for (PsiFile file : files) {
            if (file instanceof PsiJavaFile){
                PsiClass clazz = ((PsiJavaFileImpl) file).getClasses()[0];
                addAliasDesc(result,clazz, clazz.getName());
            }
        }

        return result;
    }

}
