package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.model.enums.VmTypeEnums;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm.MyTableModel;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateListPanel;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.AppTopics;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.ui.table.JBTable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author itar
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
        String[] headName = {"模板ID","模板名称", "创建人", "模板类型", "操作",""};

        SqlLiteService sqlLiteService = DbServiceFactory.getInstance(Objects.requireNonNull(project)).createSqlLiteService();

        List<Template> templates = sqlLiteService.queryTemplateList();
        Object[][] obj = new Object[templates.size()][];
        for (int i = 0; i < templates.size(); i++) {
            Template template = templates.get(i);
            JButton button = new JButton("编辑");
            JButton button2 = new JButton("初始化");
            Object[] objects = new Object[6];
            objects[0] = template.getId();
            objects[1] = template.getTepName();
            objects[2] = template.getCreateBy() == null ? "" : template.getCreateBy();
            objects[3] = VmTypeEnums.findVmNameByVmType(template.getVmType()).getMgs();
            objects[4] = button;
            objects[5] = button2;
            obj[i] = objects;
        }
        templateList.setModel(new MyTableModel(headName, obj));
        templateListForm.getMainPanel().validate();
        TemplateListPanel templateListPanel = new TemplateListPanel(project, true, templateListForm);
        templateListPanel.setTitle("编辑模板");
        addHandler(templateList, templates, project, templateListPanel);
        templateListPanel.show();
    }

    private void addHandler(JBTable table, List<Template> templates, Project project,
        TemplateListPanel templateListPanel) {
        //添加事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (column == 4) {
                    //处理button事件写在这里...
                    Integer id = (Integer) table.getValueAt(row, 0);
                    Template editingTemplate = DbServiceFactory.getInstance(project).createSqlLiteService().queryTemplateById(id);
                    if (editingTemplate==null){
                        return ;
                    }
                    AtomicReference<PsiDirectory> psiDirectory = new AtomicReference<>();
                    AtomicReference<PsiFile> file = new AtomicReference<>();
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        //先保存，再打开，再删除
                        VirtualFile vFile = null;
                        try {
                            vFile = VfsUtil.createDirectoryIfMissing(MybatisConstants.TEMP_DIR_PATH + "/tmp");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        PsiFile psiFile = PsiFileFactory.getInstance(project)
                            .createFileFromText(editingTemplate.getTepName() + ".vm", JavaFileType.INSTANCE,
                                editingTemplate.getTepContent().replaceAll("\r\n", "\n"));
                        psiDirectory.set(PsiDirectoryFactory.getInstance(project).createDirectory(vFile));
                        file.set(psiDirectory.get().findFile(editingTemplate.getTepName() + ".vm"));
                        if (file.get() != null) {
                            psiDirectory.get().delete();
                        }
                        psiDirectory.get().add(psiFile);
                        //再打开
                        PsiFile realPsiFile = Arrays.stream(psiDirectory.get().getFiles())
                            .filter(x -> x.getName().equals(editingTemplate.getTepName() + ".vm")).findAny().get();
                        new OpenFileDescriptor(project, realPsiFile.getVirtualFile()).navigateInEditor(project, true);

                        templateListPanel.doCancelAction();
                    });

                    /**
                     * 添加监听事件
                     */
                    ApplicationManager.getApplication().getMessageBus().connect()
                        .subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerListener() {

                            @Override
                            public void beforeDocumentSaving(@NotNull final Document document) {
                                final Project[] openProjects = ProjectManager.getInstance().getOpenProjects();

                                if (openProjects.length > 0) {
                                    final PsiFile psiFile = PsiDocumentManager.getInstance(openProjects[0])
                                        .getPsiFile(document);
                                    if (psiFile == null) {
                                        return;
                                    }
                                    String text = psiFile.getText();
                                    if (!psiFile.getVirtualFile().getName().startsWith(editingTemplate.getTepName())) {
                                        return;
                                    }
                                    if (StringUtils.isEmpty(document.getText())) {
                                        Messages.showErrorDialog("模板数据不可为空", "编辑模板提示");
                                        return;
                                    }
                                    if (StringUtils.isEmpty(text)) {
                                        return;
                                    }
                                    if (DigestUtils.md5Hex(text)
                                        .equals(DigestUtils.md5Hex(editingTemplate.getTepContent()))) {
                                        return;
                                    }
                                    Connection connection = ConnectionHolder.getInstance(project).getConnection(MybatisConstants.SQL_LITE_CONNECTION);
                                    if (connection == null) {
                                        return;
                                    }
                                    SqlLiteService instance = DbServiceFactory.getInstance(project).createSqlLiteService();
                                    Template updatePo = new Template();
                                    updatePo.setId(editingTemplate.getId());
                                    updatePo.setTepContent(text);
                                    instance.updateTemplate(updatePo);
                                }
                            }
                        });
                }
                //还原模板文件用
                if (column==5){
                    if (Messages.showYesNoDialog("确定要恢复当前模板吗?", "Mybatis Lite", Icons.MYBATIS_LOGO_MINI)==Messages.NO){
                        return;
                    }
                    Integer id = (Integer)table.getValueAt(row, 0);
                    SqlLiteService innerSqlLiteService = DbServiceFactory.getInstance(project).createInnerSqlLiteService();
                    Template template = innerSqlLiteService.queryTemplateById(id);
                    SqlLiteService sqlLiteService = DbServiceFactory.getInstance(project).createSqlLiteService();
                    sqlLiteService.updateTemplate(template);
                    Messages.showMessageDialog("恢复模板成功", "Mybatis Lite", Icons.MYBATIS_LOGO_MINI);
                }
            }
        });
    }


}
