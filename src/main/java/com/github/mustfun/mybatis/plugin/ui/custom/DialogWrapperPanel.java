package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.MysqlService;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.ui.UiComponentFacade;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.components.ServiceManager;
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
        CheckBoxList<Template> templateCheckbox = connectDbSetting.getTemplateCheckbox();
        List<String> collectTableBoxList = JavaUtils.collectSelectedCheckBox(tableCheckBox);
        List<Template> collectTemplateList = JavaUtils.collectSelectedCheckBox(templateCheckbox);
        if (collectTableBoxList.size() == 0 || collectTemplateList.size() == 0) {
            Messages.showErrorDialog("请至少勾选一个表和一个模板", "错误提示");
            return;
        }
        super.doOKAction();
        //tempLateList需要根据vmType排个顺序
        String tablePrefix = connectDbSetting.getTablePrefixInput().getText();
        //连接数据库
        MysqlService mysqlService = DbServiceFactory.getInstance(project).createMysqlService();
        Connection connection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.MYSQL_DB_CONNECTION);
        Connection sqlLiteConnection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.SQL_LITE_CONNECTION);
        try {
            DbServiceFactory.getInstance(project).createSqlLiteService().saveUserPreferPath(project,connectDbSetting);
            for (Object s : collectTableBoxList) {
                System.out.println("需要生成代码的表{} = " + s);
                LocalTable table = new LocalTable();
                ResultSet rs = connection.getMetaData()
                    .getTables(null, null, (String) s, new String[]{"TABLE", "VIEW"});
                while (rs.next()) {
                    table = mysqlService.initLocalTable(connection, rs);
                }
                //生成代码啦，替换模板
                mysqlService.generateCodeUseTemplate(connectDbSetting, sqlLiteConnection, table, tablePrefix,
                    collectTemplateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionHolder.getInstance(project).remove();

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
