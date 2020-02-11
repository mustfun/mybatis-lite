package com.github.mustfun.mybatis.plugin.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @update itar
 * @function 通用alt+enter检测器,继承IntentionAction接口，alt+enter唤醒
 */
public abstract class GenericIntention implements IntentionAction {

    protected IntentionChooser chooser;

    public GenericIntention(@NotNull IntentionChooser chooser) {
        this.chooser = chooser;
    }

    /**
     * alt+enter操作所在的组
     * @return
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    /**
     * 是否可用，可用时候高亮显示
     * @param project
     * @param editor
     * @param file
     * @return
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return chooser.isAvailable(project, editor, file);
    }

    /**
     * 系统在做写入操作时候是否可用
     * @return
     */
    @Override
    public boolean startInWriteAction() {
        return true;
    }

}
