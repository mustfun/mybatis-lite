package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm.*;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateListPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.sql.Connection;
import java.util.List;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/12
 * @since 1.0
 */
public class TemplateEditMenuAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        TemplateListForm templateListForm = new TemplateListForm(project);
        JBTable templateList = templateListForm.getTemplateList();
        String[] headName = {"模板名称", "创建人", "模板类型", "操作"};

        DbService dbService = DbService.getInstance(project);
        Connection connection = dbService.getSqlLiteConnection();
        SqlLiteService sqlLiteService =  SqlLiteService.getInstance(connection);
        List<Template> templates = sqlLiteService.queryTemplateList();
        Object[][] obj = new Object[templates.size()][];
        for (int i = 0; i < templates.size(); i++) {
            Template template = templates.get(i);
            JButton button = new JButton("编辑");
            Object[] objects= new Object[4];
            objects[0] = template.getTepName();
            objects[1] = template.getCreateBy()==null?"":template.getCreateBy();
            objects[2] = template.getVmType();
            objects[3] = button;
            obj[i] = objects;
        }
        templateList.setModel(new MyTableModel(headName,obj));
        templateListForm.getMainPanel().validate();
        TemplateListPanel templateListPanel = new TemplateListPanel(project, true, templateListForm);
        templateListPanel.setTitle("编辑模板");
        templateListPanel.show();
    }


}
