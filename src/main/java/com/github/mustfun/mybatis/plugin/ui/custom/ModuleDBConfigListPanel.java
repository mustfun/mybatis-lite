package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
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
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        saveConfig();
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
                saveConfig();
                UiComponentFacade.getInstance(project).buildNotify(project, "MyBatis Lite", "保存模块数据库信息成功");
            }
        };
        return new Action[]{okAction, cancelAction, applyAction};
    }

    private void saveConfig() {
        for (SingleDBConnectInfoUI singleDBConnectInfoUI : configList) {
            String moduleName = singleDBConnectInfoUI.getModuleName().getText();
            String ip = singleDBConnectInfoUI.getIpText().getText();
            String port = singleDBConnectInfoUI.getPortText().getText();
            try {
                Integer.valueOf(port);
            } catch (NumberFormatException ex) {
                UiComponentFacade.getInstance(project).buildNotify(project, "MyBatis Lite", "端口配置不正确");
                continue;
            }
            String dbName = singleDBConnectInfoUI.getDbNameText().getText();
            String userName = singleDBConnectInfoUI.getUserNameText().getText();
            String password = singleDBConnectInfoUI.getPasswordText().getText();
            SqlLiteService sqlLiteService = DbServiceFactory.getInstance(project).createSqlLiteService();
            sqlLiteService.insertDbConnectionInfo(new DbSourcePo(ip, Integer.valueOf(port), dbName, userName, password,moduleName));
        }
    }
}
