package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.util.MybatisDomUtils;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.AutoPopupControllerImpl;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.AppUIExecutor;
import com.intellij.openapi.application.impl.AppUIExecutorImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @update itar 主要是自动补全用
 * @function 自动补全的监控
 */
public class MybatisTypedHandler extends TypedHandlerDelegate {

    /**
     * 自动弹出候选项,写.的时候弹出
     */
    @NotNull
    @Override
    public Result checkAutoPopup(char charTyped, @NotNull final Project project, @NotNull final Editor editor, @NotNull PsiFile file) {
        //在xml里面打个. 唤醒自动补全
      PsiFile topLevelFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
      if (charTyped == '.' && MybatisDomUtils.isMybatisFile(topLevelFile)) {
            autoPopupParameter(project, editor);
            return Result.STOP;
        }
        return super.checkAutoPopup(charTyped, project, editor, file);
    }


    /**
     * 当特殊符号被输入时候被唤醒 作用条件: #{}  唤醒自动补全 系统会自动去找 CompletionContributor
     *
     * @see com.intellij.codeInsight.completion.CompletionContributor
     */
    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        int index = editor.getCaretModel().getOffset() - 2;
        //比如sqlFile 顶层 file 就是xmlFile
        PsiFile topLevelFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
        boolean parameterCase = c == '{' &&
            index >= 0 &&
            editor.getDocument().getText().charAt(index) == '#' &&
            file instanceof SqlFile &&
            MybatisDomUtils.isMybatisFile(topLevelFile);
        if (parameterCase) {
            autoPopupParameter(project, editor);
            return Result.STOP;
        }
        return super.charTyped(c, project, editor, file);
    }

    /**
     * 当特殊符号输入之前唤醒
     */
    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file,
        @NotNull FileType fileType) {
        return super.beforeCharTyped(c, project, editor, file, fileType);
    }

    private static void autoPopupParameter(final Project project, final Editor editor) {
      //AutoPopupControllerImpl
      AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, CompletionType.BASIC,
              new Condition<PsiFile>() {
                @Override
                public boolean value(PsiFile psiFile) {
                  return true;
                }
              });

//      AppUIExecutorImpl appUIExecutor = (AppUIExecutorImpl) AppUIExecutor.onUiThread().withDocumentsCommitted(project);
//      appUIExecutor.later().inTransaction(project).execute(() -> {
//          //当前文件是否最新，没有未提交的
//          if (PsiDocumentManager.getInstance(project).isCommitted(editor.getDocument())) {
//              //唤醒代码自动补全 ， CompletionContributor
//            CodeCompletionHandlerBase codeCompletionHandlerBase = new CodeCompletionHandlerBase(CompletionType.BASIC);
//            codeCompletionHandlerBase.invokeCompletion(project, editor, 1);
//            //帮忙回收吧
//            codeCompletionHandlerBase = null;
//          }
//      });
    }


}