package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.ui.UiGenerateUtil;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import java.util.Objects;

/**
 * @author itar
 * @date 2020-03-17
 *
 */
public class ProjectRightPopupAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Module module = e.getDataContext().getData(LangDataKeys.MODULE);
        DialogWrapperPanel commonDialog = UiGenerateUtil.getInstance(Objects.requireNonNull(project)).getCommonDialog(module==null?project.getName():module.getName());
        commonDialog.show();
    }
}
