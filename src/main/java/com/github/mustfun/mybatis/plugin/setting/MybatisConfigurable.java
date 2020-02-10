package com.github.mustfun.mybatis.plugin.setting;

import static com.github.mustfun.mybatis.plugin.generate.StatementGenerator.DELETE_GENERATOR;
import static com.github.mustfun.mybatis.plugin.generate.StatementGenerator.INSERT_GENERATOR;
import static com.github.mustfun.mybatis.plugin.generate.StatementGenerator.SELECT_GENERATOR;
import static com.github.mustfun.mybatis.plugin.generate.StatementGenerator.UPDATE_GENERATOR;

import com.github.mustfun.mybatis.plugin.generate.GenerateModel;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;


/**
 * @author yanglin
 * @update itar
 * @function 配置相关
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private MybatisSetting mybatisSetting;

    private MybatisSettingForm mybatisSettingForm;

    private String separator = ";";

    private Splitter splitter = Splitter.on(separator).omitEmptyStrings().trimResults();

    private Joiner joiner = Joiner.on(separator);

    public MybatisConfigurable() {
        mybatisSetting = MybatisSetting.getInstance();
    }

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

    Boolean navi = false;

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisSettingForm();
        }
        boolean naviOpenStatus = PropertiesComponent.getInstance().getBoolean("naviOpenStatus");
        boolean notFirstIn = PropertiesComponent.getInstance().getBoolean("notFirstIn");
        if (!notFirstIn) {
            if (!naviOpenStatus) {
                naviOpenStatus = true;
                PropertiesComponent.getInstance().setValue("naviOpenStatus", true);
            }
        }
        if (naviOpenStatus) {
            mybatisSettingForm.openNaviButton.setSelected(true);
            mybatisSettingForm.closeNaviRadioButton.setSelected(false);
        } else {
            mybatisSettingForm.closeNaviRadioButton.setSelected(true);
            mybatisSettingForm.openNaviButton.setSelected(false);
        }
        return mybatisSettingForm.mainPanel;
    }

    @Override
    public boolean isModified() {
        boolean naviOpenStatus = PropertiesComponent.getInstance().getBoolean("naviOpenStatus");
        return mybatisSetting.getStatementGenerateModel().getIdentifier() != mybatisSettingForm.modelComboBox
            .getSelectedIndex()
            || !joiner.join(INSERT_GENERATOR.getPatterns()).equals(mybatisSettingForm.insertPatternTextField.getText())
            || !joiner.join(DELETE_GENERATOR.getPatterns()).equals(mybatisSettingForm.deletePatternTextField.getText())
            || !joiner.join(UPDATE_GENERATOR.getPatterns()).equals(mybatisSettingForm.updatePatternTextField.getText())
            || !joiner.join(SELECT_GENERATOR.getPatterns()).equals(mybatisSettingForm.selectPatternTextField.getText())
            || naviOpenStatus != mybatisSettingForm.openNaviButton.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        mybatisSetting
            .setStatementGenerateModel(GenerateModel.getInstance(mybatisSettingForm.modelComboBox.getSelectedIndex()));
        INSERT_GENERATOR
            .setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.insertPatternTextField.getText())));
        DELETE_GENERATOR
            .setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.deletePatternTextField.getText())));
        UPDATE_GENERATOR
            .setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.updatePatternTextField.getText())));
        SELECT_GENERATOR
            .setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.selectPatternTextField.getText())));
        PropertiesComponent.getInstance()
            .setValue("naviOpenStatus", mybatisSettingForm.getOpenNaviButton().isSelected());
        PropertiesComponent.getInstance().setValue("notFirstIn", true);
    }

    @Override
    public void reset() {
        mybatisSettingForm.modelComboBox.setSelectedIndex(mybatisSetting.getStatementGenerateModel().getIdentifier());
        mybatisSettingForm.insertPatternTextField.setText(joiner.join(INSERT_GENERATOR.getPatterns()));
        mybatisSettingForm.deletePatternTextField.setText(joiner.join(DELETE_GENERATOR.getPatterns()));
        mybatisSettingForm.updatePatternTextField.setText(joiner.join(UPDATE_GENERATOR.getPatterns()));
        mybatisSettingForm.selectPatternTextField.setText(joiner.join(SELECT_GENERATOR.getPatterns()));
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
