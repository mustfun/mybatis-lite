package com.github.mustfun.mybatis.plugin.alias;

import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author yanglin
 * @updater itar
 * @function 依赖转换用的服务类
 */
public class AliasFacade {

    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private List<AliasResolver> resolvers;

    public static final AliasFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AliasFacade.class);
    }

    public AliasFacade(Project project) {
        this.project = project;
        this.resolvers = Lists.newArrayList();
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        initResolvers();
    }

    /**
     * 初始化解析类
     */
    private void initResolvers() {
        try {
            //spring扩展的一个类，理论上通过serviceManager应该也是可以拿到的
            Class.forName("com.intellij.spring.model.utils.SpringModelUtils");
            this.registerResolver(AliasResolverFactory.createBeanResolver(project));
        } catch (ClassNotFoundException e) {
        }
        this.registerResolver(AliasResolverFactory.createSingleAliasResolver(project));
        this.registerResolver(AliasResolverFactory.createConfigPackageResolver(project));
        this.registerResolver(AliasResolverFactory.createAnnotationResolver(project));
        this.registerResolver(AliasResolverFactory.createInnerAliasResolver(project));
    }

    /**
     * 利用jetbrains提供的javaPsiFacade内部方法找到一个类
     * @param element
     * @param shortName  全限定名
     * @return
     */
    @NotNull
    public Optional<PsiClass> findPsiClass(@Nullable PsiElement element, @NotNull String shortName) {
        PsiClass clazz = javaPsiFacade.findClass(shortName, GlobalSearchScope.allScope(project));
        if (null != clazz) {
            return Optional.of(clazz);
        }
        //如果传递的是一个list这样的别名过来了，那么就不能通过全限定名找到
        for (AliasResolver resolver : resolvers) {
            Set<AliasDesc> classAliasDescriptions = resolver.getClassAliasDescriptions(element);
            for (AliasDesc desc : classAliasDescriptions) {
                if (desc.getAlias().equalsIgnoreCase(shortName)) {
                    return Optional.of(desc.getClazz());
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    public Collection<AliasDesc> getAliasDescs(@Nullable PsiElement element) {
        ArrayList<AliasDesc> result = Lists.newArrayList();
        for (AliasResolver resolver : resolvers) {
            result.addAll(resolver.getClassAliasDescriptions(element));
        }
        return result;
    }

    public Optional<AliasDesc> findAliasDesc(@Nullable PsiClass clazz) {
        if (null == clazz) {
            return Optional.empty();
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(clazz)) {
                if (desc.getClazz().equals(clazz)) {
                    return Optional.of(desc);
                }
            }
        }
        return Optional.empty();
    }

    public void registerResolver(@NotNull AliasResolver resolver) {
        this.resolvers.add(resolver);
    }

}
