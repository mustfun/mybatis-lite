package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.listener.CheckMouseListener;
import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateCodeEditPanel;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import com.intellij.ui.CheckBoxList;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.util.*;

/**
 * @author itar
 * @date 2018/6/12
 * @version 1.0
 * @since jdk1.8
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
        UiComponentFacade uiComponentFacade = UiComponentFacade.getInstance(project);
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
            }
            templateCheckbox.addMouseListener(new CheckMouseListener(project,1,templates.get(2)));
        });

        JButton daoPanel = connectDbSetting.getDaoButton();
        daoPanel.addActionListener(e -> {
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("Select target folder", baseDir, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getDaoInput().setText(path);
        });

        JButton mapperButton = connectDbSetting.getMapperButton();

        mapperButton.addActionListener(e->{
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("Select target folder", baseDir, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getDaoInput().setText(path);
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
