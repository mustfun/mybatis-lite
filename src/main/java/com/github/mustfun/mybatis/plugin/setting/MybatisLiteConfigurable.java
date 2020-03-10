package com.github.mustfun.mybatis.plugin.setting;

import com.github.mustfun.mybatis.plugin.listener.SettingApplyListener;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.options.SearchableConfigurable;
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
public class MybatisLiteConfigurable implements SearchableConfigurable {

    private Map<String,String> rawStateMap;

    private MybatisSettingForm mybatisSettingForm;

    private String separator = ";";


    public MybatisLiteConfigurable() {
        rawStateMap = Objects.requireNonNull(MybatisLiteSetting.getInstance().getState()).map;
    }

    @NotNull
    @Override
    public String getId() {
        return "Mybatis Lite";
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
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisSettingForm();
        }
        return mybatisSettingForm.mainPanel;
    }

    @Override
    public boolean isModified() {
        Map<String, String> afterMap = transDataToMap();
        return needUpdate(rawStateMap, afterMap);
    }

    @Override
    public void apply(){
        Map<String, String> newMap = transDataToMap();
        MybatisLiteSetting.getInstance().loadState(new MybatisLiteSetting.MybatisLiteState(newMap));
        new SettingApplyListener().notify(newMap,rawStateMap);
        rawStateMap = newMap;
    }

    @Override
    public void reset() {
        mybatisSettingForm.getInsertPatternInput().setText(rawStateMap.get(MybatisConstants.DEFAULT_INSERT_PATTEN_KEY));
        mybatisSettingForm.getDeletePattenInput().setText(rawStateMap.get(MybatisConstants.DEFAULT_DELETE_PATTEN_KEY));
        mybatisSettingForm.getUpdatePatternInput().setText(rawStateMap.get(MybatisConstants.DEFAULT_UPDATE_PATTEN_KEY));
        mybatisSettingForm.getSelectPattenInput().setText(rawStateMap.get(MybatisConstants.DEFAULT_SELECT_PATTEN_KEY));
        boolean open = MybatisConstants.TRUE.equalsIgnoreCase(rawStateMap.get(MybatisConstants.NAVIGATION_OPEN_STATUS));
        if (open) {
            mybatisSettingForm.openNaviButton.setSelected(true);
            mybatisSettingForm.closeNaviRadioButton.setSelected(false);
        }else{
            mybatisSettingForm.openNaviButton.setSelected(false);
            mybatisSettingForm.closeNaviRadioButton.setSelected(true);
        }
        boolean sqlField = MybatisConstants.TRUE.equalsIgnoreCase(rawStateMap.get(MybatisConstants.SQL_FIELD_STATUS));
        if(sqlField){
            mybatisSettingForm.getSqlFieldOpenButton().setSelected(true);
            mybatisSettingForm.getSqlFieldCloseButton().setSelected(false);
        }else{
            mybatisSettingForm.getSqlFieldOpenButton().setSelected(false);
            mybatisSettingForm.getSqlFieldCloseButton().setSelected(true);
        }
        mybatisSettingForm.getBootConfigNameTextField().setText(rawStateMap.get(MybatisConstants.CONFIG_FILE_NAME));
    }


    /**
     * 如果有新配置项这里要改
     * @return
     */
    public Map<String,String> transDataToMap(){
        Map<String, String> map = new HashMap<>();
        map.put(MybatisConstants.DEFAULT_INSERT_PATTEN_KEY,mybatisSettingForm.getInsertPatternInput().getText());
        map.put(MybatisConstants.DEFAULT_DELETE_PATTEN_KEY,mybatisSettingForm.getDeletePattenInput().getText());
        map.put(MybatisConstants.DEFAULT_UPDATE_PATTEN_KEY,mybatisSettingForm.getUpdatePatternInput().getText());
        map.put(MybatisConstants.DEFAULT_SELECT_PATTEN_KEY,mybatisSettingForm.getSelectPattenInput().getText());
        map.put(MybatisConstants.NAVIGATION_OPEN_STATUS, mybatisSettingForm.openNaviButton.isSelected() ? MybatisConstants.TRUE : "0");
        map.put(MybatisConstants.SQL_FIELD_STATUS, mybatisSettingForm.getSqlFieldOpenButton().isSelected() ? MybatisConstants.TRUE : "0");
        map.put(MybatisConstants.CONFIG_FILE_NAME, mybatisSettingForm.getBootConfigNameTextField().getText());
        return map;
    }

    public Boolean needUpdate(Map<String,String> beforeMap,Map<String,String> afterMap){
        for (String s : beforeMap.keySet()) {
            if (!afterMap.get(s).equalsIgnoreCase(beforeMap.get(s))){
                return true;
            }
        }
        return false;
    }

}
