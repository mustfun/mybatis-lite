package com.github.mustfun.mybatis.plugin.setting.endpoint;

import com.github.mustfun.mybatis.plugin.listener.SettingApplyListener;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author itar
 * @function 配置相关， 新增配置 transDataToMap 方法和reset方法小改一下就可以了，很方便
 * 主要存储project级别的配置
 */
public class MybatisLiteProjectExtendConfigurableUi implements ConfigurableUi<MybatisLiteProjectExtendSetting> {

    private JPanel myRootPanel;
    private Map<String, String> rawStateMap;
    private Project project;

    public MybatisLiteProjectExtendConfigurableUi(Project project) {
        rawStateMap = Objects.requireNonNull(MybatisLiteProjectExtendSetting.getInstance(project).getState()).map;
        this.project = project;
        this.setupUI();
    }

    private void setupUI() {
        myRootPanel = new JPanel();
        myRootPanel.add(new JButton("test"));
    }


    @NotNull
    @Override
    public JComponent getComponent() {
        return this.myRootPanel;
    }


    @Override
    public void reset(@NotNull MybatisLiteProjectExtendSetting settings) {

    }

    @Override
    public boolean isModified(@NotNull MybatisLiteProjectExtendSetting settings) {
        Map<String, String> afterMap = transDataToMap();
        return needUpdate(rawStateMap, afterMap);
    }

    @Override
    public void apply(@NotNull MybatisLiteProjectExtendSetting settings) {
        Map<String, String> newMap = transDataToMap();
        MybatisLiteProjectExtendSetting.getInstance(project).loadState(new MybatisLiteProjectExtendSetting(newMap));
        new SettingApplyListener().notify(newMap, rawStateMap);
        rawStateMap = newMap;
    }




    /**
     * 如果有新配置项这里要改
     *
     * @return
     */
    public Map<String, String> transDataToMap() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    public Boolean needUpdate(Map<String, String> beforeMap, Map<String, String> afterMap) {
        for (String s : beforeMap.keySet()) {
            if (!afterMap.get(s).equalsIgnoreCase(beforeMap.get(s))) {
                return true;
            }
        }
        return false;
    }

}
