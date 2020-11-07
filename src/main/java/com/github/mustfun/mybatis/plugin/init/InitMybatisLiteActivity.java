package com.github.mustfun.mybatis.plugin.init;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.ModuleConfig;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.service.resolver.AbstractFileResolver;
import com.github.mustfun.mybatis.plugin.service.resolver.ResolverFacade;
import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.github.mustfun.mybatis.plugin.util.crypto.ConfigTools;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spring.boot.run.SpringBootApplicationConfigurationType;
import com.intellij.spring.boot.run.SpringBootApplicationRunConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author itar
 * @date 2020-03-02
 * 项目初始化时候调用   等以后版本提高了之后可以使用DumbAware接口，版本要求193+
 */
public class InitMybatisLiteActivity implements StartupActivity {

    private static final Logger LOG = LoggerFactory.getLogger(InitMybatisLiteActivity.class);

    /**
     *
     * @param project
     */
    @Override
    public void runActivity(@NotNull Project project) {
        initProjectConfig(project);
        initProjectRunSetting(project);
    }

    private void initProjectRunSetting(Project project) {
        Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
        if (!MybatisConstants.TRUE.equalsIgnoreCase(valueMap.get(MybatisConstants.SQL_PRINT_STATUS))) {
            return;
        }
        List<RunnerAndConfigurationSettings> configurationSettingsList = RunManager.getInstance(project)
                .getConfigurationSettingsList(SpringBootApplicationConfigurationType.class);
        if (CollectionUtils.isEmpty(configurationSettingsList)) {
          LOG.error("【Mybatis Lite】初始化项目配置失败");
          return;
        }
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = configurationSettingsList.get(0);
        SpringBootApplicationRunConfiguration configuration = (SpringBootApplicationRunConfiguration) runnerAndConfigurationSettings.getConfiguration();

        if (StringUtils.isEmpty(configuration.getVMParameters())) {
            configuration.setVMParameters("-javaagent:" + MybatisConstants.PLUGIN_LIB_PATH + "/" + JavaUtils.getPluginFullNameWithExtension());
        } else if (configuration.getVMParameters().contains("mybatis-lite")) {
            return;
        } else {
            configuration.setVMParameters(configuration.getVMParameters() + "     -javaagent:" + MybatisConstants.PLUGIN_LIB_PATH + "/" + JavaUtils.getPluginFullNameWithExtension());
        }
        return;
    }

    private void initProjectConfig(@NotNull Project project) {
        try {
            Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
            if (!MybatisConstants.TRUE.equalsIgnoreCase(valueMap.get(MybatisConstants.SQL_FIELD_STATUS))) {
                return ;
            }
            Map<String, DbSourcePo> stringDbSourcePoMap = initDatabase(project);
            if (!stringDbSourcePoMap.isEmpty()) {
                for (String s : stringDbSourcePoMap.keySet()) {
                    ConnectionHolder.getInstance(project).putConfig(s,stringDbSourcePoMap.get(s));
                }
            }
            //然后刷新下DB缓存
            SqlLiteService sqlLiteService = DbServiceFactory.getInstance(project).createSqlLiteService();
            sqlLiteService.refreshFromDB();
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
        List<AbstractFileResolver<VirtualFile, Properties>> fileResolvers = ResolverFacade.getInstance(project).getFileResolvers();
        for (Module module : modules) {
            VirtualFile moduleFile = module.getModuleFile();
            if(moduleFile==null){
                continue;
            }
            for (AbstractFileResolver<VirtualFile, Properties> fileResolver : fileResolvers) {
                Properties resolve = fileResolver.resolve(moduleFile.getParent());
                if(resolve==null||resolve.size()==0){
                    continue;
                }
                addToConfigMap(map, module,resolve);
            }

        }
        return map;
    }

    /**
     * 组装configMap
     * @param map
     * @param module
     * @param resolve
     */
    private void addToConfigMap(Map<String, DbSourcePo> map, Module module,Properties resolve) {
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
        moduleConfig.setUrl(masterUrl==null?url:masterUrl);

        String value = moduleConfig.getUrl();
        String[] s = value.split("/");
        String[] split = s[2].split(":");
        moduleConfig.setDbAddress(split[0]);
        moduleConfig.setPort(Integer.valueOf(split[1]));
        String s1 = s[3].split("\\?")[0];
        moduleConfig.setDbName(s1);

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
}
