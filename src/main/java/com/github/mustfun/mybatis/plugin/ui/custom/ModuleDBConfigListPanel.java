package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.setting.ModuleDBConfigUI;
import com.github.mustfun.mybatis.plugin.setting.SingleDBConnectInfoUI;
import com.github.mustfun.mybatis.plugin.ui.UiComponentFacade;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author itar
 * @version 1.0
 * @date 2020/03/18
 * @since 1.0
 * @function DB配置弹出框
 */
public class ModuleDBConfigListPanel extends DialogWrapper {

    private ModuleDBConfigUI dbConfigUI;
    private Project project;
    private List<SingleDBConnectInfoUI> configList;

    public ModuleDBConfigListPanel(@Nullable Project project, boolean canBeParent, ModuleDBConfigUI templateEdit, List<SingleDBConnectInfoUI> uiList) {
        super(project, canBeParent);
        this.project = project;
        this.dbConfigUI = templateEdit;
        this.configList = uiList;
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
        System.out.println("cancel");
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        System.out.println("ok");
        super.doOKAction();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = getOKAction();
        Action cancelAction = getCancelAction();
        Action applyAction = new AbstractAction("Apply") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //保存用户填写的信息，顺便弹个框
                for (SingleDBConnectInfoUI singleDBConnectInfoUI : configList) {

                }
                UiComponentFacade.getInstance(project).buildNotify(project, "MyBatis Lite", "保存模块数据库信息成功");
            }
        };
        return new Action[]{okAction, cancelAction, applyAction};
    }
}
