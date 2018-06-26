package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm.*;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateListPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.*;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/12
 * @since 1.0
 */
public class TemplateEditMenuAction extends AnAction {

    private JButton[] buttons;


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        TemplateListForm templateListForm = new TemplateListForm(project);
        JBTable templateList = templateListForm.getTemplateList();
        String headName[] = {"模板名称", "创建人", "模板类型", "编辑"};

        buttons = new JButton[5];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("" + i);
        }
        Object obj[][] = {
                {"LiMing", 23, Boolean.TRUE, buttons[0]},
                {"ZhangSan", 25, Boolean.TRUE, buttons[1]},
                {"WangWu", 21, Boolean.FALSE, buttons[2]},
                {"LiSi", 28, Boolean.TRUE, buttons[3]},
                {"LuBo", 20, Boolean.FALSE, buttons[4]}};

        templateList.setModel(new MyTableModel(headName,obj));
        templateListForm.getMainPanel().validate();
        TemplateListPanel templateListPanel = new TemplateListPanel(project, true, templateListForm);
        templateListPanel.setTitle("编辑模板");
        templateListPanel.show();
    }


}
