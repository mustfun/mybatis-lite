package com.github.mustfun.mybatis.plugin.init;

import com.github.mustfun.mybatis.plugin.service.resolver.YamlFileResolver;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spring.profiles.SpringProfile;
import com.intellij.spring.profiles.SpringProfilesFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author itar
 * @date 2020-03-02
 * 项目初始化时候调用
 */
public class InitMybatisLiteActivity implements StartupActivity {

    /**
     *
     * @param project
     */
    @Override
    public void runActivity(@NotNull Project project) {
        long l = System.currentTimeMillis();
        try {
            initDatabase(project);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化大于10s为了不影响用户体验，建议关掉
        if (System.currentTimeMillis()-l>10000){
            //Messages.showMessageDialog("建议到Mybatis Lite设置中关掉本sql字段提示选项", "[Mybatis Lite]系统连接数据库超时", Messages.getErrorIcon());
        }
    }

    /**
     * 初始化数据库连接等
     * @param project
     */
    private void initDatabase(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            VirtualFile moduleFile = module.getModuleFile();
            if(moduleFile==null){
                continue;
            }
            YamlFileResolver yamlFileResolver = new YamlFileResolver();
            yamlFileResolver.resolve(moduleFile.getParent());
        }
    }
}
