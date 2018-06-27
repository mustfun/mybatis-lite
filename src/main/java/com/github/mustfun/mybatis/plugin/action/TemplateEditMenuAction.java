package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.model.enums.VmTypeEnums;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm;
import com.github.mustfun.mybatis.plugin.setting.TemplateListForm.MyTableModel;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateListPanel;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.AppTopics;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
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
        ConnectionHolder.addConnection(MybatisConstants.SQL_LITE_CONNECTION,connection);

        List<Template> templates = sqlLiteService.queryTemplateList();
        Object[][] obj = new Object[templates.size()][];
        for (int i = 0; i < templates.size(); i++) {
            Template template = templates.get(i);
            JButton button = new JButton("编辑");
            Object[] objects= new Object[4];
            objects[0] = template.getTepName();
            objects[1] = template.getCreateBy()==null?"":template.getCreateBy();
            objects[2] = VmTypeEnums.findVmNameByVmType(template.getVmType()).getMgs();
            objects[3] = button;
            obj[i] = objects;
        }
        templateList.setModel(new MyTableModel(headName,obj));
        templateListForm.getMainPanel().validate();
        TemplateListPanel templateListPanel = new TemplateListPanel(project, true, templateListForm);
        templateListPanel.setTitle("编辑模板");
        addHandler(templateList,templates,project,templateListPanel);
        templateListPanel.show();
    }

    private void addHandler(JBTable table, List<Template> templates, Project project, TemplateListPanel templateListPanel){
        //添加事件
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if(column==3){
                    //处理button事件写在这里...
                    String tepName = (String) table.getValueAt(row, 0);
                    Template template = templates.stream().filter(x -> x.getTepName().equals(tepName)).findAny().get();
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        //先保存，再打开，再删除
                        VirtualFile vFile = null;
                        try {
                            vFile = VfsUtil.createDirectoryIfMissing(MybatisConstants.TEMP_DIR_PATH + "/tmp");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(template.getTepName() + ".vm", JavaFileType.INSTANCE, template.getTepContent().replaceAll("\r\n", "\n"));
                        PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(vFile);
                        PsiFile file = psiDirectory.findFile(template.getTepName() + ".vm");
                        if (file==null){
                            psiDirectory.add(psiFile);
                        }
                        //再打开
                        PsiFile realPsiFile = Arrays.stream(psiDirectory.getFiles()).filter(x -> x.getName().equals(template.getTepName() + ".vm")).findAny().get();
                        new OpenFileDescriptor(project, realPsiFile.getVirtualFile()).navigateInEditor(project, true);

                        templateListPanel.doCancelAction();
                    });

                    /**
                     * 添加监听事件
                     */
                    ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {

                        @Override
                        public void beforeDocumentSaving(@NotNull final Document document) {
                            final Project[] openProjects = ProjectManager.getInstance().getOpenProjects();

                            if (openProjects.length > 0) {
                                final PsiFile psiFile = PsiDocumentManager.getInstance(openProjects[0]).getPsiFile(document);
                                String text = psiFile.getText();
                                if (StringUtils.isEmpty(text)){
                                    return ;
                                }
                                if (StringUtils.isEmpty(document.getText())){
                                    Messages.showErrorDialog("模板数据不可为空", "编辑模板提示");
                                    return ;
                                }
                                if (DigestUtils.md5(text).equals(DigestUtils.md5(template.getTepContent()))){
                                    return ;
                                }
                                Connection connection = ConnectionHolder.getConnection(MybatisConstants.SQL_LITE_CONNECTION);
                                SqlLiteService instance = SqlLiteService.getInstance(connection);
                                Template updatePo = new Template();
                                updatePo.setId(template.getId());
                                updatePo.setTepContent(text);
                                instance.updateTemplate(updatePo);

                            }
                        }
                    });
                }
            }
        });
    }


}
