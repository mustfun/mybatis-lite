package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.util.DomUtils;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.editorActions.CompletionAutoPopupHandler;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;


import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @update itar
 */
public class MybatisTypedHandler extends TypedHandlerDelegate {

    /**
     * 自动弹出候选项,写.的时候弹出
     * @param charTyped
     * @param project
     * @param editor
     * @param file
     * @return
     */
    @Override
    public Result checkAutoPopup(char charTyped, final Project project, final Editor editor, PsiFile file) {
        //在xml里面打个. 唤醒自动补全，有个蛋用啊...
        if (charTyped == '.' && DomUtils.isMybatisFile(file)) {
            autoPopupParameter(project, editor);
            return Result.STOP;
        }
        return super.checkAutoPopup(charTyped, project, editor, file);
    }

    /**
     * 当特殊符号被输入时候被唤醒
     * @param c
     * @param project
     * @param editor
     * @param file
     * @return
     */
    @Override
    public Result charTyped(char c,@NotNull final Project project, @NotNull final Editor editor, @NotNull PsiFile file) {
        int index = editor.getCaretModel().getOffset() - 2;
        PsiFile topLevelFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
        boolean parameterCase = c == '{' &&
                index >= 0 &&
                editor.getDocument().getText().charAt(index) == '#' &&
                file instanceof SqlFile &&
                DomUtils.isMybatisFile(topLevelFile);
        if (parameterCase) {
            autoPopupParameter(project, editor);
            return Result.STOP;
        }
        return super.charTyped(c, project, editor, file);
    }

    private static void autoPopupParameter(final Project project, final Editor editor) {
        AutoPopupController.runTransactionWithEverythingCommitted(project, new Runnable() {
            @Override
            public void run() {
                //当前文件是否最新，没有未提交的
                if (PsiDocumentManager.getInstance(project).isCommitted(editor.getDocument())) {
                    //唤醒代码自动补全
                    new CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor, 1);
                }
            }
        });
    }

}