package com.github.mustfun.mybatis.plugin.ui.custom;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.EditorTextField;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author itar
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class TemplateCodeEditPanel extends DialogWrapper {

    private TemplateEdit templateEdit;
    private Template template;
    private Project project;

    public TemplateCodeEditPanel(@Nullable Project project, boolean canBeParent, TemplateEdit templateEdit,Template template) {
        super(project, canBeParent);
        this.templateEdit = templateEdit;
        this.template = template;
        this.project = project;
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return templateEdit.getMainPanel();
    }

    /**
     * 点击确认之后，持久化到磁盘里面吧
     */
    @Override
    protected void doOKAction() {
        super.doOKAction();
        EditorTextField editorTextField = templateEdit.getEditorTextField();
        String text = editorTextField.getText();
        if(StringUtils.isEmpty(text)){
            Messages.showMessageDialog("模板修改成功", "模板修改提示", Messages.getErrorIcon());
            return;
        }
        System.out.println("template = " + template.getTepName());
        SqlLiteService sqlLiteService = DbServiceFactory.getInstance(project).createSqlLiteService();
        if (template.getTepContent().equals(text)) {
            return ;
        }
        template.setTepContent(text);
        sqlLiteService.updateTemplate(template);
        Messages.showMessageDialog("模板修改成功", "模板修改提示", Messages.getInformationIcon());

    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }
}
