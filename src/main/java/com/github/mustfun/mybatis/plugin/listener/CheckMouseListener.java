package com.github.mustfun.mybatis.plugin.listener;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateCodeEditPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckBoxList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.UnsupportedEncodingException;

/**
 * @author : itar
 * @date : 2018-06-17
 * @time : 17:19
 * @Version: 1.0
 * @since: JDK 1.8
 */
public class CheckMouseListener implements MouseListener {

    private Integer checkBoxId;
    private Project project;
    private Template template;

    public CheckMouseListener(Project project,Integer checkBoxId,Template template){
        this.checkBoxId = checkBoxId;
        this.project = project;
        this.template = template;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int clickTimes = e.getClickCount();
        CheckBoxList list = (CheckBoxList) e.getSource();
        if (clickTimes == 2) {
            TemplateEdit templateEdit = new TemplateEdit();
            templateEdit.getCodeArea().setText(template.getTepContent());
            TemplateCodeEditPanel templateCodeEditPanel = new TemplateCodeEditPanel(project, true, templateEdit);
            templateCodeEditPanel.show();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
