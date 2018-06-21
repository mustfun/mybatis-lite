package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class DialogWrapperPanel extends DialogWrapper {

    private ConnectDbSetting connectDbSetting;

    public DialogWrapperPanel(@Nullable Project project, boolean canBeParent, ConnectDbSetting connectDbSetting) {
        super(project, canBeParent);
        this.connectDbSetting = connectDbSetting;
        setTitle("数据库信息");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return connectDbSetting.getMainPanel();
    }

    @Override
    protected void doOKAction() {

        //然后做自己的事情,准备生成代码了
        CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
        CheckBoxList<Integer> templateCheckbox = connectDbSetting.getTemplateCheckbox();
        List collectTableBoxList = JavaUtils.collectSelectedCheckBox(tableCheckBox);
        List collectTemplateList = JavaUtils.collectSelectedCheckBox(templateCheckbox);

        for (Object o : collectTableBoxList) {
            System.out.println("o = " + o);
        }

        for (Object o : collectTemplateList) {
            System.out.println("o = " + o);
        }

        //super.doOKAction();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        setOKButtonText("生成");
        return super.getOKAction();
    }

    @NotNull
    @Override
    protected Action getCancelAction() {
        setCancelButtonText("取消");
        return super.getCancelAction();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }
}
