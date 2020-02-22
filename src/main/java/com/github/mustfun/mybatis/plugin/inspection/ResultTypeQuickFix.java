package com.github.mustfun.mybatis.plugin.inspection;

import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author yanglin
 * @updater itar
 * @function 快速修复返回值不匹配
 */
public class ResultTypeQuickFix extends GenericQuickFix {

    public static final String CORRECT_RESULTTYPE = "修正返回类型";
    private Select select;
    private SmartPsiElementPointer<PsiClass> targetPointer;

    public ResultTypeQuickFix(@NotNull Select select, @NotNull PsiClass target) {
        this.select = select;
        this.targetPointer = SmartPointerManager.getInstance(target.getProject()).createSmartPsiElementPointer(target, target.getContainingFile());
    }

    @NotNull
    @Override
    public String getName() {
        return CORRECT_RESULTTYPE;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        GenericAttributeValue<PsiClass> resultType = select.getResultType();
        resultType.setValue(targetPointer.getElement());
    }

    @NotNull
    public PsiClass getTarget() {
        return Objects.requireNonNull(targetPointer.getElement());
    }


    @NotNull
    public Select getSelect() {
        return select;
    }

    public void setSelect(@NotNull Select select) {
        this.select = select;
    }
}
