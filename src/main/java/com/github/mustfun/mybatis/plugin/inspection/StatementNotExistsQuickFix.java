package com.github.mustfun.mybatis.plugin.inspection;

import com.github.mustfun.mybatis.plugin.generate.AbstractStatementGenerator;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author yanglin
 * @updater itar
 * @function xml不存在的一个快速修复
 */
public class StatementNotExistsQuickFix extends GenericQuickFix {

    public static final String GENERATE_STATEMENT = "生成SQL语句";
    private SmartPsiElementPointer<PsiMethod> methodPointer;

    public StatementNotExistsQuickFix(@NotNull PsiMethod method) {
        methodPointer = SmartPointerManager.getInstance(method.getProject()).createSmartPsiElementPointer(method, method.getContainingFile());
    }

    @NotNull
    @Override
    public String getName() {
        return GENERATE_STATEMENT;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        AbstractStatementGenerator.applyGenerate(methodPointer.getElement());
    }

    @NotNull
    public PsiMethod getMethod() {
        return Objects.requireNonNull(methodPointer.getElement());
    }
}
