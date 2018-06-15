package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateCodeEditPanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.CheckBoxList;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.util.List;

/**
 * @author yanglin
 */
public final class UiGenerateUtil {

    private Project project;

    private FileEditorManager fileEditorManager;

    private ConnectDbSetting connectDbSetting;

    private UiGenerateUtil(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static UiGenerateUtil getInstance(@NotNull Project project) {
        return new UiGenerateUtil(project);
    }

    public DialogWrapperPanel getCommonDialog(){
        if (null == connectDbSetting) {
            this.connectDbSetting = new ConnectDbSetting();
        }
        connectDbSetting.getConnectButton().addActionListener(e -> {
            //监听点击
            String address = connectDbSetting.getAddress().getText();
            String port = connectDbSetting.getPort().getText();
            String dbName = connectDbSetting.getDbName().getText();
            String userName = connectDbSetting.getUserName().getText();
            String password = connectDbSetting.getPassword().getText();
            DbSourcePo dbSourcePo = new DbSourcePo();
            dbSourcePo.setDbAddress(address);
            dbSourcePo.setDbName(dbName);
            dbSourcePo.setPort(Integer.parseInt(port));
            dbSourcePo.setUserName(userName);
            dbSourcePo.setPassword(password);
            //连接数据库
            DbService dbService = DbService.getInstance(project);
            Connection connection = dbService.getConnection(dbSourcePo);
            if (connection == null) {
                Messages.showMessageDialog("数据库连接失败", "连接数据库提示", Messages.getInformationIcon());
                return;
            }else {
                //Messages.showMessageDialog("连接成功", "连接数据库提示", Messages.getInformationIcon());
            }
            List<LocalTable> tables = dbService.getTables(connection);
            CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
            for (LocalTable table : tables) {
                tableCheckBox.addItem(table.getTableName(),table.getTableName(),true);
            }
            Connection sqlLiteConnection = dbService.getSqlLiteConnection(dbSourcePo);
            SqlLiteService sqlLiteService =  SqlLiteService.getInstance(sqlLiteConnection);
            List<Template> templates = sqlLiteService.queryTemplateList();
            CheckBoxList templateCheckbox = connectDbSetting.getTemplateCheckbox();
            for (Template template : templates) {
                templateCheckbox.addItem(template.getId(),template.getTepName(),true);
                templateCheckbox.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        new TemplateCodeEditPanel(project,true,new TemplateEdit()).show();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {

                    }
                });
            }
        });

        return new DialogWrapperPanel(project,true,connectDbSetting);
    }

    public JBPopup getCommonPopUp(){
        if (null == connectDbSetting) {
            this.connectDbSetting = new ConnectDbSetting();
        }

        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(connectDbSetting.getMainPanel(), null)
                /*
                .setResizable(false)
                .setShowShadow(true)
                .setCancelKeyEnabled(true)
                .setShowBorder(true)*/
                .setTitle("生成MapperXml文件")
                .setShowBorder(true)
                .setCancelButton(new IconButton("关闭",AllIcons.Actions.Close))
                .setRequestFocus(true)
                .setFocusable(true)
                .setMovable(false)
                .setCancelOnOtherWindowOpen(true)
                .setCancelOnClickOutside(false)
                .setProject(this.project)
                .createPopup();
        return popup;
    }



    @NotNull
    private BalloonBuilder buildBalloon(JComponent component) {
        JBInsets BORDER_INSETS = JBUI.insets(20, 20, 20, 20);
        return JBPopupFactory.getInstance()
                .createDialogBalloonBuilder(component, null)
                .setHideOnClickOutside(true)
                .setShadow(true)
                .setBlockClicksThroughBalloon(true)
                .setRequestFocus(true)
                .setBorderInsets(BORDER_INSETS);
    }
}
