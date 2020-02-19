package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * [Mybatis Lite]生成@Getter和@Setter注解
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
        return "[Mybatis Lite]生成@Getter和@Setter注解";
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
        if (element == null) {
            return false;
        }
        if (element.getParent() instanceof PsiJavaFile) {
            return analyseClassHasSetterAndGetter(element.getNextSibling());
        }

        //直接在类上面的情况
        if (element.getParent() instanceof PsiClass) {
            element = element.getNextSibling();
        } else {
            element = element.getParent();
        }
        //目前这种方式就是最优解--我看了IDEA原生实现，只能在class上面实现shortcut  ==== ，在注解周围的情况
        if (element instanceof PsiModifierList) {
            PsiElement findElement = element.getParent();
            if (findElement == null) {
                return false;
            }
            return analyseClassHasSetterAndGetter(findElement);
        }

        return false;


    }

    @NotNull
    private Boolean analyseClassHasSetterAndGetter(PsiElement findElement) {
        if (findElement instanceof PsiClass) {
            PsiClass clazz = (PsiClass) findElement;
            if (JavaUtils.isAnnotationPresent(clazz, Annotation.GETTER)
                && JavaUtils.isAnnotationPresent(clazz, Annotation.SETTER)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (element.getParent() instanceof PsiClass) {
            element = element.getParent();
        } else if (element.getNextSibling() instanceof PsiClass) {
            element = element.getNextSibling();
        } else if (element.getParent().getParent() instanceof PsiClass) {
            element = element.getParent().getParent();
        } else {
            return;
        }

        PsiClass clazz = (PsiClass) element;

        boolean hasGetter = JavaUtils.isAnnotationPresent(clazz, Annotation.GETTER);
        boolean hasSetter = JavaUtils.isAnnotationPresent(clazz, Annotation.SETTER);

        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();

        if (!hasGetter) {
            JavaService.getInstance(clazz.getProject())
                .importClazz((PsiJavaFile) clazz.getContainingFile(), Annotation.GETTER.getQualifiedName());
            PsiAnnotation psiGetterAnnotation = elementFactory
                .createAnnotationFromText(Annotation.GETTER.toString(), clazz);
            // find near class
            clazz.addBefore(psiGetterAnnotation,
                JavaUtils.findNealModifierElement(clazz.getFirstChild()).getFirstChild());
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiGetterAnnotation);
        }
        if (!hasSetter) {
            JavaService.getInstance(clazz.getProject())
                .importClazz((PsiJavaFile) clazz.getContainingFile(), Annotation.SETTER.getQualifiedName());
            PsiAnnotation psiGetterAnnotation = elementFactory
                .createAnnotationFromText(Annotation.SETTER.toString(), clazz);
            clazz.addBefore(psiGetterAnnotation,
                JavaUtils.findNealModifierElement(clazz.getFirstChild()).getFirstChild());
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiGetterAnnotation);
        }
        //将全限定名转换为非全限定名
        CodeStyleManager.getInstance(project).reformat(clazz);
    }

    PsiElement afterAddHandler(PsiElement element, PsiElement anchor) {
        final PsiElement newLineNode =
            PsiParserFacade.SERVICE.getInstance(element.getProject()).createWhiteSpaceFromText("\n\n");
        anchor.getParent().addBefore(newLineNode, anchor);
        return anchor;
    }


    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
