package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.service.AnnotationService;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @update by itar alt+enter 响应 , 添加@Param注解
 */
public class GenerateParamAnnotationIntention extends GenericIntention {

    public static final String ADD_MYBATIS_PARAM_ANNOTATION = "add Mybatis @Param annotation";

    public GenerateParamAnnotationIntention() {
        super(GenerateParamChooser.INSTANCE);
    }

    @NotNull
    @Override
    public String getText() {
        return ADD_MYBATIS_PARAM_ANNOTATION;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        AnnotationService annotationService = AnnotationService.getInstance(project);
        if (null != parameter) {
            annotationService.addAnnotationWithParameterName(parameter);
        } else {
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (null != method) {
                annotationService.addAnnotationWithParameterNameForMethodParameters(method);
            }
        }
    }

}
