package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.ui.UiComponentFacade;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CheckBoxList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * 生成代码核心逻辑，点击确认后系统调用
     */
    @Override
    protected void doOKAction() {

        //然后做自己的事情,准备生成代码了
        CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
        CheckBoxList<Integer> templateCheckbox = connectDbSetting.getTemplateCheckbox();
        List collectTableBoxList = JavaUtils.collectSelectedCheckBox(tableCheckBox);
        List collectTemplateList = JavaUtils.collectSelectedCheckBox(templateCheckbox);
        if (collectTableBoxList.size() == 0 || collectTemplateList.size() == 0) {
            Messages.showErrorDialog("请至少勾选一个表和一个模板", "错误提示");
            return;
        }
        super.doOKAction();
        //tempLateList需要根据vmType排个顺序
        String tablePrefix = connectDbSetting.getTablePrefixInput().getText();
        //连接数据库
        DbService dbService = DbService.getInstance(project);
        Connection connection = ConnectionHolder.getConnection(MybatisConstants.MYSQL_DB_CONNECTION);
        Connection sqlLiteConnection = ConnectionHolder.getConnection(MybatisConstants.SQL_LITE_CONNECTION);
        try {
            for (Object s : collectTableBoxList) {
                System.out.println("需要生成代码的表{} = " + s);
                LocalTable table = new LocalTable();
                ResultSet rs = connection.getMetaData()
                    .getTables(null, null, (String) s, new String[]{"TABLE", "VIEW"});
                while (rs.next()) {
                    table = dbService.initLocalTable(connection, rs);
                }
                //生成代码啦，替换模板
                dbService.generateCodeUseTemplate(connectDbSetting, sqlLiteConnection, table, tablePrefix,
                    collectTemplateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object o : collectTemplateList) {
            System.out.println("o = " + o);
        }

        ConnectionHolder.remove();

        UiComponentFacade uiComponentFacade = UiComponentFacade.getInstance(project);
        uiComponentFacade.buildNotify(project, "Mybatis Lite", "生成代码成功");

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
