package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.service.AnnotationService;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
            PsiClass clazz = (PsiClass) list.getNextSibling().getParent();
            if (parent == null) {
                return;
            }
            boolean hasGetter = JavaUtils.isAnnotationPresent(clazz, Annotation.GETTER);
            boolean hasSetter = JavaUtils.isAnnotationPresent(clazz, Annotation.SETTER);
            List<Annotation> generateList = new ArrayList<>();
            if (!hasGetter){
                generateList.add(Annotation.GETTER);
            }
            if (!hasSetter){
                generateList.add(Annotation.SETTER);
            }
            for (Annotation annotation : generateList) {
                generateAnnotation(project, parent, clazz, annotation);
            }
        }
    }

    private void generateAnnotation(@NotNull Project project, PsiWhiteSpace parent, PsiClass clazz,Annotation annotation) {
        JavaService.getInstance(parent.getProject()).importClazz((PsiJavaFile) clazz.getContainingFile(), annotation.getQualifiedName());

        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiAnnotation psiGetterAnnotation = elementFactory.createAnnotationFromText(annotation.toString(), clazz);
        clazz.addAfter(psiGetterAnnotation,parent);
        clazz.addAfter(parent,psiGetterAnnotation);
        //将全限定名转换为非全限定名
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiGetterAnnotation);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
