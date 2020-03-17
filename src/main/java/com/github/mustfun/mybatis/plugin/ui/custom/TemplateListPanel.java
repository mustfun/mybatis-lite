package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nullable;

/**
 * @author itar
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class TemplateListPanel extends DialogWrapper {

    private TemplateListForm templateEdit;

    public TemplateListPanel(@Nullable Project project, boolean canBeParent, TemplateListForm templateEdit) {
        super(project, canBeParent);
        this.templateEdit = templateEdit;
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return templateEdit.getMainPanel();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }
}
