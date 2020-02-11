package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar 自定义alt+enter选择框
 */
public abstract class JavaFileIntentionChooser implements IntentionChooser {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        return null != element && JavaUtils.isElementWithinInterface(element) && isAvailable(element);
    }

    /**
     * 是否可用方法
     * @param element
     * @return
     */
    public abstract boolean isAvailable(@NotNull PsiElement element);

    /**
     * 当前元素是不是一个参数
     * @param element
     * @return
     */
    public boolean isPositionOfParameterDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiParameter;
    }

    /**
     * 当前元素是不是一个方法
     * @param element
     * @return
     */
    public boolean isPositionOfMethodDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiMethod;
    }

    /**
     * 当前元素是不是一个类
     * @param element
     * @return
     */
    public boolean isPositionOfInterfaceDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiClass;
    }

    /**
     * 当前元素是不是在xml里面
     * @param element
     * @return
     */
    public boolean isTargetPresentInXml(@NotNull PsiElement element) {
        return JavaService.getInstance(element.getProject()).findWithFindFirstProcessor(element).isPresent();
    }

}
