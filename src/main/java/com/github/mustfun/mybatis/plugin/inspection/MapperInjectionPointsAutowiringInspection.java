package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.utils.SpringAutowireUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Mapper注解报错提示 - 暂不使用
 * @see com.intellij.spring.model.highlighting.autowire.SpringUastInjectionPointsAutowiringInspection
 * @author itar
 *
 */
public class MapperInjectionPointsAutowiringInspection  extends AbstractBaseJavaLocalInspectionTool {

    @Nullable
    @Override
    public ProblemDescriptor[] checkField(@NotNull PsiField psiField, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (SpringAutowireUtil.isAutowiringRelevantClass(psiField.getContainingClass()) && SpringAutowireUtil.isAutowiredByAnnotation(psiField)) {
            CommonSpringModel model = SpringAutowireUtil.getProcessingSpringModel(psiField.getContainingClass());
            PsiElement sourcePsi = psiField.getSourceElement();
            if (model != null && sourcePsi != null) {
                ProblemsHolder holder = new ProblemsHolder(manager, sourcePsi.getContainingFile(), isOnTheFly);
                checkInjectionPoint(psiField, psiField.getType(), holder, model, SpringAutowireUtil.isRequired(psiField));
                return holder.getResultsArray();
            }
        }
        return null;
    }


    private void checkInjectionPoint(PsiField psiField, PsiType psiType, ProblemsHolder holder, CommonSpringModel model, boolean required) {
        if (!psiType.isValid()) {
            return ;
        }
        if (psiField == null) {
            return ;
        }
        PsiAnnotation annotation = SpringAutowireUtil.getEffectiveQualifiedAnnotation(psiField);
        if (annotation!=null) {
            checkQualifiedAutowiring(psiType, annotation, holder, model);
        }else{
            checkByTypeAutowire(psiField, psiType, holder, model, required);
        }

    }

    /**
     *
     * @param psiField
     * @param psiType
     * @param holder
     * @param model
     * @param required
     */
    private void checkByTypeAutowire(PsiField psiField, PsiType psiType, ProblemsHolder holder, CommonSpringModel model, boolean required) {
        if (!psiField.getSourceElement().getTextRange().isEmpty()) {
            String primaryCandidateName = psiField.getName();
            Set<SpringBeanPointer> beanPointers = SpringAutowireUtil.autowireByType(model, psiType, primaryCandidateName);
            Set<SpringBeanPointer> iterableBeanPointers = SpringAutowireUtil.getIterableBeanPointers(psiType, model, primaryCandidateName);
            if (beanPointers.isEmpty() && iterableBeanPointers.isEmpty() && required) {
                if (holder != null && !SpringAutowireUtil.isAutowiredByDefault(psiType)  && !SpringAutowireUtil.isJavaUtilOptional(psiType)) {
                    holder.registerProblem(psiField, getBeansNotFoundMessage(psiType), new LocalQuickFix[0]);
                }
            }
        }
    }

    private static String getBeansNotFoundMessage(@NotNull PsiType searchType) {
        PsiType secondarySearchType = SpringAutowireUtil.getIterableSearchType(searchType);
        return secondarySearchType != null ? SpringBundle.message("bean.autowiring.by.type.no.beans", new Object[]{secondarySearchType.getPresentableText(), searchType.getPresentableText()}) : SpringBundle.message("bean.autowiring.by.type.none", new Object[]{searchType.getPresentableText()});
    }

    /**
     *
     * @param psiType
     * @param annotation
     * @param holder
     * @param model
     */
    private void checkQualifiedAutowiring(PsiType psiType, PsiAnnotation annotation, ProblemsHolder holder, CommonSpringModel model) {

    }
}
