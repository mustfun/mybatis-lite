package com.github.mustfun.mybatis.plugin.setting;

import com.github.mustfun.mybatis.plugin.listener.SettingApplyListener;
import com.github.mustfun.mybatis.plugin.setting.endpoint.AbstractEndpointTabConfigurable;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author itar
 * @function 配置相关， 新增配置 transDataToMap 方法和reset方法小改一下就可以了，很方便
 */
public class MybatisLiteProjectConfigurable implements SearchableConfigurable {

    private Map<String,String> rawStateMap;

    private MybatisLiteProjectSettingUI mybatisSettingForm;

    private Project project;

    public MybatisLiteProjectConfigurable(Project project) {
        rawStateMap = Objects.requireNonNull(MybatisLiteProjectSetting.getInstance(project).getState()).map;
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return "Auto Config";
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId()+"Mybatis Lite 项目级别配置";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisLiteProjectSettingUI();
        }
        /*EndpointTabConfigurable[] endpointTabConfigurables = getEndpointTabConfigurables();
        int row=0;
        for (EndpointTabConfigurable tabConfigurable : endpointTabConfigurables) {
            JComponent component = tabConfigurable.createComponent();
            JPanel panel = (JPanel)component;
            panel.setBorder(IdeBorderFactory.createTitledBorder(tabConfigurable.getDisplayName()));
            GridConstraints constraint = new GridConstraints();
            constraint.setRow(row++);
            constraint.setIndent(1);
            constraint.setAnchor(9);
            mybatisSettingForm.mainPanel.add(component,constraint);
        }*/
        return mybatisSettingForm.getMainPanel();

    }

    @NotNull
    private AbstractEndpointTabConfigurable[] getEndpointTabConfigurables() {
        return AbstractEndpointTabConfigurable.EP_NAME.getExtensions();
    }

    @Override
    public boolean isModified() {
        Map<String, String> afterMap = transDataToMap();
        return needUpdate(rawStateMap, afterMap);
    }

    @Override
    public void apply(){
        Map<String, String> newMap = transDataToMap();
        MybatisLiteProjectSetting.getInstance(project).loadState(new MybatisLiteProjectSetting.MybatisLiteProjectState(newMap));
        new SettingApplyListener().notify(newMap,rawStateMap);
        rawStateMap = newMap;
    }

    @Override
    public void reset() {
        mybatisSettingForm.getBootConfigNameTextField().setText(rawStateMap.get(MybatisConstants.CONFIG_FILE_NAME));
    }


    /**
     * 如果有新配置项这里要改
     * @return
     */
    public Map<String,String> transDataToMap(){
        Map<String, String> map = new HashMap<>();
        map.put(MybatisConstants.CONFIG_FILE_NAME, mybatisSettingForm.getBootConfigNameTextField().getText());
        return map;
    }

    public Boolean needUpdate(Map<String,String> beforeMap,Map<String,String> afterMap){
        for (String s : beforeMap.keySet()) {
            if (!beforeMap.get(s).equalsIgnoreCase(afterMap.get(s))){
                return true;
            }
        }
        return false;
    }

}
