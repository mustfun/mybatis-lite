package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.listener.CheckMouseListener;
import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckBoxList;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.sql.Connection;
import java.util.List;

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
            ConnectionHolder.addConnection("mysqlDbConnection",connection);
            if (connection == null) {
                Messages.showMessageDialog("数据库连接失败", "连接数据库提示", Messages.getInformationIcon());
                return;
            }else {
                //Messages.showMessageDialog("连接成功", "连接数据库提示", Messages.getInformationIcon());
            }
            List<LocalTable> tables = dbService.getTables(connection);
            CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
            for (LocalTable table : tables) {
                tableCheckBox.addItem(table.getTableName(),table.getTableName(),false);
            }
            Connection sqlLiteConnection = dbService.getSqlLiteConnection();
            ConnectionHolder.addConnection("sqlLiteConnection",sqlLiteConnection);
            SqlLiteService sqlLiteService =  SqlLiteService.getInstance(sqlLiteConnection);
            List<Template> templates = sqlLiteService.queryTemplateList();
            CheckBoxList<Integer> templateCheckbox = connectDbSetting.getTemplateCheckbox();
            for (Template template : templates) {
                templateCheckbox.addItem(template.getId(),template.getTepName(),false);
            }
            templateCheckbox.addMouseListener(new CheckMouseListener(project,1,templates.get(2)));
        });

        JButton daoPanel = connectDbSetting.getDaoButton();
        daoPanel.addActionListener(e -> {
            VirtualFile baseDir = project.getBaseDir();
            //找出dao层所在目录
            VirtualFile daoPath = JavaUtils.getFilePattenPath(baseDir, "Mapper.java","Dao.java");
            if (daoPath==null){
                daoPath = baseDir;
            }
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择dao层存放目录", daoPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getDaoInput().setText(path);
        });

        JButton mapperButton = connectDbSetting.getMapperButton();

        mapperButton.addActionListener(e->{
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile mapperPath = JavaUtils.getFilePattenPath(baseDir, "Mapper.xml","Dao.xml","mapper/");
            if (mapperPath==null){
                mapperPath = baseDir;
            }
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择Mapper层存放目录", mapperPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getMapperInput().setText(path);
        });

        connectDbSetting.getPoButton().addActionListener(e->{
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile mapperPath = JavaUtils.getFilePattenPath(baseDir, "model/");
            if (mapperPath==null){
                mapperPath = baseDir;
            }
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择实体层存放目录", mapperPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getPoInput().setText(path);
        });

        // service
        connectDbSetting.getServiceButton().addActionListener(e->{
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile mapperPath = JavaUtils.getFilePattenPath(baseDir, "service.java","service/");
            if (mapperPath==null){
                mapperPath = baseDir;
            }
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择Service层存放目录", mapperPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getServiceInput().setText(path);
        });

        //controller
        connectDbSetting.getControllerButton().addActionListener(e->{
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile mapperPath = JavaUtils.getFilePattenPath(baseDir, "controller.java","controller/");
            if (mapperPath==null){
                mapperPath = baseDir;
            }
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择Controller层存放目录", mapperPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getControllerInput().setText(path);
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
