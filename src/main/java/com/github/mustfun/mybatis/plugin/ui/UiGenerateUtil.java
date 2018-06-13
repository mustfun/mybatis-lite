package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
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
            String userName = connectDbSetting.getUserName().getText();
            String password = connectDbSetting.getPassword().getText();
            DbSourcePo dbSourcePo = new DbSourcePo();
            dbSourcePo.setDbAddress(address);
            //dbSourcePo.setDbName();

            DbService.getInstance(project).getConnection(dbSourcePo);
            Messages.showMessageDialog("连接成功", "连接数据库提示", Messages.getInformationIcon());

            CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
            tableCheckBox.addItem("address1",address,false);
            tableCheckBox.addItem("address2",address,false);
            tableCheckBox.addItem("address3",address,false);
            tableCheckBox.addItem("address4",address,false);
            tableCheckBox.addListSelectionListener(e1 -> {
                String itemAt = tableCheckBox.getItemAt(e1.getFirstIndex());
                Messages.showMessageDialog(itemAt, "连接数据库提示", Messages.getInformationIcon());
            });

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
