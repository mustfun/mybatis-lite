package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateListPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

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
        JBList templateList = templateListForm.getTemplateList();
        Vector vector = new Vector();
        vector.add("data1");
        vector.add("data2");
        vector.add("data3");
        vector.add("data4");
        templateList.setListData(vector);
        JPanel buttonPanel = templateListForm.getButtonPanel();
        buttonPanel.add(new Button("button1"));
        buttonPanel.add(new Button("button2"));
        buttonPanel.add(new Button("button3"));
        buttonPanel.add(new Button("button4"));
        buttonPanel.validate();
        TemplateListPanel templateListPanel = new TemplateListPanel(project, true, templateListForm);
        templateListPanel.show();
    }

}
