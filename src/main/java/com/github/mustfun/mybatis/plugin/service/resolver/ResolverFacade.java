package com.github.mustfun.mybatis.plugin.service.resolver;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 解析类总类
 * @author itar
 * @date 2020-03-08
 */
public class ResolverFacade {

    private List<AbstractFileResolver<VirtualFile, Properties>> fileResolvers;
    private Project project;

    public ResolverFacade(Project project) {
        initFileResolvers(project);
    }

    public static ResolverFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ResolverFacade.class);
    }

    private void initFileResolvers(Project project) {
        fileResolvers = new ArrayList<>();
        fileResolvers.add(new YamlFileResolver(project));
        fileResolvers.add(new PropertiesFileResolver(project));
        fileResolvers.add(new XmlConfigFileResolver(project));
    }

    public List<AbstractFileResolver<VirtualFile, Properties>> getFileResolvers() {
        return fileResolvers;
    }
}
