package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class DialogWrapperPanel extends DialogWrapper {

    private ConnectDbSetting connectDbSetting;
    private Project project;

    public DialogWrapperPanel(@Nullable Project project, boolean canBeParent, ConnectDbSetting connectDbSetting) {
        super(project, canBeParent);
        this.project = project;
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
        //tempLateList需要根据vmType排个顺序
        
        String packageName = connectDbSetting.getPackageInput().getText();
        //连接数据库
        DbService dbService = DbService.getInstance(project);
        Connection connection = ConnectionHolder.getConnection("mysqlDbConnection");
        Connection sqlLiteConnection = ConnectionHolder.getConnection("sqlLiteConnection");
        try {
            for (Object s : collectTableBoxList) {
                System.out.println("需要生成代码的表{} = " + s);
                LocalTable table = new LocalTable();
                ResultSet rs = connection.getMetaData().getTables(null,null,(String) s, new String[]{"TABLE","VIEW"});
                while (rs.next()) {
                    table = dbService.initLocalTable(connection, rs);
                }
                //生成代码啦，替换模板
                dbService.generateCodeUseTemplate(connectDbSetting,sqlLiteConnection,table,packageName,collectTemplateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object o : collectTemplateList) {
            System.out.println("o = " + o);
        }

        //super.doOKAction();
        //ConnectionHolder.remove();
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
