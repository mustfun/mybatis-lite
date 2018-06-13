package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
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
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnClickOutside(false)
                .createPopup();
        connectDbSetting.getConnectButton().addActionListener(e -> {
            //监听点击
            String address = connectDbSetting.getAddress().getText();
            String port = connectDbSetting.getPort().getText();
            String userName = connectDbSetting.getUserName().getText();
            String password = connectDbSetting.getPassword().getText();

            connectDbSetting.getTableCheckBox().addItem("address",address,false);
            connectDbSetting.getTableCheckBox().addItem("address",address,false);
            connectDbSetting.getTableCheckBox().addItem("address",address,false);
            connectDbSetting.getTableCheckBox().addItem("address",address,false);

        });
        return popup;
    }

}
