package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author itar
 * @version 1.0
 * @date 2020/02/26
 * @since 1.0
 *
 */
public class TemplateViewPanel extends DialogWrapper {

    private TemplateEdit templateEdit;

    public TemplateViewPanel(@Nullable Project project, boolean canBeParent, TemplateEdit templateEdit) {
        super(project, canBeParent);
        this.templateEdit = templateEdit;
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return templateEdit.getMainPanel();
    }
}
