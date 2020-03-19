package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.setting.ModuleDBConfigUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author itar
 * @version 1.0
 * @date 2020/03/18
 * @since 1.0
 * @function DB配置弹出框
 */
public class ModuleDBConfigListPanel extends DialogWrapper {

    private ModuleDBConfigUI dbConfigUI;

    public ModuleDBConfigListPanel(@Nullable Project project, boolean canBeParent, ModuleDBConfigUI templateEdit) {
        super(project, canBeParent);
        this.dbConfigUI = templateEdit;
        setTitle("各模块数据库信息");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return dbConfigUI.getMainPanel();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }
}
