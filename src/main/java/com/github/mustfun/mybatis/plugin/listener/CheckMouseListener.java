package com.github.mustfun.mybatis.plugin.listener;

import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateCodeEditPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckBoxList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

    public CheckMouseListener(Project project,Integer checkBoxId){
        this.checkBoxId = checkBoxId;
        this.project = project;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int clickTimes = e.getClickCount();
        CheckBoxList list = (CheckBoxList) e.getSource();
        if (clickTimes == 2) {
            new TemplateCodeEditPanel(project, true, new TemplateEdit()).show();
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
