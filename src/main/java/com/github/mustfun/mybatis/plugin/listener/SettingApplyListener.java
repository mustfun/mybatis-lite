package com.github.mustfun.mybatis.plugin.listener;

import com.github.mustfun.mybatis.plugin.init.InitMybatisLiteActivity;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.util.Map;

import static com.github.mustfun.mybatis.plugin.util.MybatisConstants.*;

/**
 * @author itar
 * @date 2020-03-09
 * 设置保存的时候唤醒
 */
public class SettingApplyListener {

    /**
     *
     * @param newMap
     * @param rawStateMap
     */
    public void notify(Map<String, String> newMap, Map<String, String> rawStateMap) {
        String configNameNew = newMap.get(CONFIG_FILE_NAME);
        String configNameRaw = rawStateMap.get(CONFIG_FILE_NAME);
        if (configNameNew==null){
           return ;
        }
        if (configNameNew.equalsIgnoreCase(configNameRaw)){
            return;
        }
        Project[] defaultProject = ProjectManager.getInstance().getOpenProjects();
        for (Project project : defaultProject) {
            new InitMybatisLiteActivity().runActivity(project);
        }
    }
}
