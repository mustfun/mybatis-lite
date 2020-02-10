package com.github.mustfun.mybatis.plugin.inspection;

import com.github.mustfun.mybatis.plugin.generate.StatementGenerator;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class StatementNotExistsQuickFix extends GenericQuickFix {

    private PsiMethod method;

    public StatementNotExistsQuickFix(@NotNull PsiMethod method) {
        this.method = method;
    }

    @NotNull
    @Override
    public String getName() {
        return "Generate statement";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        StatementGenerator.applyGenerate(method);
    }

    @NotNull
    public PsiMethod getMethod() {
        return method;
    }

    public void setMethod(@NotNull PsiMethod method) {
        this.method = method;
    }

}
