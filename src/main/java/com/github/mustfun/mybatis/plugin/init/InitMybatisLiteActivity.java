package com.github.mustfun.mybatis.plugin.init;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.ModuleConfig;
import com.github.mustfun.mybatis.plugin.service.resolver.YamlFileResolver;
import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.github.mustfun.mybatis.plugin.util.crypto.ConfigTools;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
        if (!MybatisConstants.TRUE.equalsIgnoreCase(valueMap.get(MybatisConstants.SQL_FIELD_STATUS))) {
            return ;
        }
        try {
            Map<String, DbSourcePo> stringDbSourcePoMap = initDatabase(project);
            if (!stringDbSourcePoMap.isEmpty()) {
                for (String s : stringDbSourcePoMap.keySet()) {
                    ConnectionHolder.getInstance(project).putConfig(s,stringDbSourcePoMap.get(s));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据库连接等
     * @param project
     */
    private Map<String, DbSourcePo> initDatabase(Project project) {
        Map<String, DbSourcePo> map = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        boolean multiModule = modules.length>1;
        for (Module module : modules) {
            VirtualFile moduleFile = module.getModuleFile();
            if(moduleFile==null||(module.getName().equals(project.getName())&&multiModule)){
                continue;
            }
            YamlFileResolver yamlFileResolver = new YamlFileResolver();
            Properties resolve = yamlFileResolver.resolve(moduleFile.getParent());
            if(resolve==null){
                continue;
            }
            String masterUserName=null,userName=null,masterPassword=null,password=null,masterUrl=null,url=null,typeAliasPackage=null;
            for (Object o : resolve.keySet()) {
                String key = o.toString();
                String value = resolve.get(o).toString();
                //解析数据库
                if (key.contains("database")&&(key.contains("username")|| key.contains("user"))){
                    if (key.contains("master")) {
                        masterUserName = value;
                    }else{
                        userName = value;
                    }
                }
                if (key.contains("database")&&(key.contains("password"))){
                    if (key.contains("master")){
                        masterPassword = value;
                    }else {
                        password = value;
                    }
                }
                if (key.contains("database")&&(key.contains("url"))){
                    if (key.contains("master")){
                        masterUrl = value;
                    }else{
                        url = value;
                    }
                }
                //解析mybatis配置文件
                if (key.contains("mybatis")&&key.contains("type")&&key.contains("aliases")&&key.contains("package")) {
                    typeAliasPackage = value;
                }
            }
            ModuleConfig moduleConfig = new ModuleConfig();
            moduleConfig.setDbAddress(masterUrl==null?url:masterUrl);
            moduleConfig.setUserName(masterUserName==null?userName:masterUserName);
            moduleConfig.setPassword(masterPassword==null?password:masterPassword);
            try {
                moduleConfig.setPassword(moduleConfig.getPassword().length()>64? ConfigTools.decrypt(moduleConfig.getPassword()):moduleConfig.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
            moduleConfig.setTypeAliasPackage(typeAliasPackage);
            map.put(module.getName(), moduleConfig);
        }
        return map;
    }
}
