package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.service.AnnotationService;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/11
 * @since 1.0
 */
public class LombokIntentionAction implements IntentionAction {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "[Lombok] generate @Getter and @Setter";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        //如果不是javaFile , 就不显示
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        //拿到当前位置
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (element==null){
            return false;
        }
        //目前这种方式就是最优解--我看了IDEA原生实现，只能在class上面实现shortcut
        if (element.getParent() instanceof PsiModifierList) {
            PsiElement findElement = element.getParent();
            if (findElement.getNextSibling()==null){
                return false;
            }

            if (findElement.getNextSibling().getParent() instanceof PsiClass){
                PsiClass clazz = (PsiClass) findElement.getNextSibling().getParent();
                if (JavaUtils.isAnnotationPresent(clazz, Annotation.GETTER)
                        &&JavaUtils.isAnnotationPresent(clazz, Annotation.SETTER)){
                    return false;
                }
                return true;
            }
        }
        return false;



    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiModifierList list = (PsiModifierList) element.getParent();
        if (list!=null){
            PsiElement[] children = list.getChildren();
            PsiWhiteSpace parent = null;
            for (PsiElement child : children) {
                if (child instanceof PsiWhiteSpace){
                    parent = (PsiWhiteSpace)child;
                }
            }
            if (parent == null) {
                return;
            }
            JavaService.getInstance(parent.getProject()).importClazz((PsiJavaFile) parent.getContainingFile(), Annotation.GETTER.getQualifiedName());
            JavaService.getInstance(parent.getProject()).importClazz((PsiJavaFile) parent.getContainingFile(), Annotation.SETTER.getQualifiedName());

            PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
            PsiAnnotation psiGetterAnnotation = elementFactory.createAnnotationFromText(Annotation.GETTER.toString(), parent);
            PsiAnnotation psiSetterAnnotation = elementFactory.createAnnotationFromText(Annotation.SETTER.toString(), parent);
            parent.add(psiGetterAnnotation);
            parent.add(psiSetterAnnotation);
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiGetterAnnotation.getParent());
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
