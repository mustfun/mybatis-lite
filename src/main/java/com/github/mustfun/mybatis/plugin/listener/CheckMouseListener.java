package com.github.mustfun.mybatis.plugin.listener;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.setting.TemplateEdit;
import com.github.mustfun.mybatis.plugin.ui.custom.TemplateCodeEditPanel;
import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.util.DiffDrawUtil;
import com.intellij.diff.util.TextDiffType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.EditorTextField;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.intellij.diff.util.DiffUtil.getDiffType;

/**
 * @author : itar
 * @date : 2018-06-17
 * @time : 17:19
 * @Version: 1.0
 * @since: JDK 1.8
 */
public class CheckMouseListener implements MouseListener {

    private Project project;

    public CheckMouseListener(Project project) {
        this.project = project;
    }

    /**
     * EditorTextFieldProvider用这个也挺方便的
     * @param e
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked(MouseEvent e) {
        int clickTimes = e.getClickCount();
        CheckBoxList<Template> list = (CheckBoxList<Template>) e.getSource();
        Template template = list.getItemAt(list.locationToIndex(e.getPoint()));
        if(template==null){
            return ;
        }
        if (clickTimes == 2) {
            TemplateEdit templateEdit = new TemplateEdit();
            EditorTextField editorTextField = templateEdit.getEditorTextField();
            String tepContent = template.getTepContent();
            PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText(template.getTepName(), JavaLanguage.INSTANCE, tepContent);
            editorTextField.setDocument(PsiDocumentManager.getInstance(project).getDocument(fileFromText));
            editorTextField.setBorder(null);
            editorTextField.setOneLineMode(false);
            editorTextField.ensureWillComputePreferredSize();
            //字体跟随系统设置大小
            editorTextField.setFontInheritedFromLAF(false);
            //定位到头部
            editorTextField.setCaretPosition(0);
            editorTextField.addSettingsProvider(uEditor -> {
                uEditor.setVerticalScrollbarVisible(true);
                uEditor.setHorizontalScrollbarVisible(true);

                //renderMode一开就不可以编辑了，谨慎开启
                //uEditor.setRendererMode(true);
                //uEditor.setBorder(null);
                uEditor.setCaretVisible(true);
                uEditor.setCaretEnabled(true);
                uEditor.setInsertMode(true);
                //uEditor.setColorsScheme(editor.getColorsScheme());
                //uEditor.setBackgroundColor(backgroundColor);
                //uEditor.getSettings().setCaretRowShown(false);
                uEditor.getSettings().setLineNumbersShown(true);


                //uEditor.getSettings().setTabSize(editor.getSettings().getTabSize(editor.getProject()));
                //uEditor.getSettings().setUseTabCharacter(editor.getSettings().isUseTabCharacter(editor.getProject()));

                uEditor.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(JavaFileType.INSTANCE, EditorColorsManager.getInstance().getGlobalScheme(),project));
            });
            TemplateCodeEditPanel templateCodeEditPanel = new TemplateCodeEditPanel(project, true, templateEdit);
            templateCodeEditPanel.setTitle("查看模板");
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
