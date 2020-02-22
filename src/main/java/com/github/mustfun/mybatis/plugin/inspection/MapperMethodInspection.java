package com.github.mustfun.mybatis.plugin.inspection;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.github.mustfun.mybatis.plugin.generate.AbstractStatementGenerator;
import com.github.mustfun.mybatis.plugin.locator.MapperLocator;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yanglin
 * @updater itar
 * @function mapper方法缺失时候下标飘红
 */
public class MapperMethodInspection extends AbstractMapperInspection {

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager,
        boolean isOnTheFly) {
        //判断如果不是@select等注解或者 不是标准的dao层文件就退出
        if (!MapperLocator.getInstance(method.getProject()).process(method) || JavaUtils
            .isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES)) {
            return EMPTY_ARRAY;
        }
        List<ProblemDescriptor> res = createProblemDescriptors(method, manager, isOnTheFly);
        return res.toArray(new ProblemDescriptor[0]);
    }

    private List<ProblemDescriptor> createProblemDescriptors(PsiMethod method, InspectionManager manager,
        boolean isOnTheFly) {
        ArrayList<ProblemDescriptor> res = Lists.newArrayList();
        //检查语句是否存在
        Optional<ProblemDescriptor> p1 = checkStatementExists(method, manager, isOnTheFly);
        p1.ifPresent(res::add);
        //检查返回值是否正常
        Optional<ProblemDescriptor> p2 = checkResultType(method, manager, isOnTheFly);
        p2.ifPresent(res::add);
        return res;
    }

    private Optional<ProblemDescriptor> checkResultType(PsiMethod method, InspectionManager manager,
                                                                  boolean isOnTheFly) {
        //找到相应的sql语句
        Optional<DomElement> ele = JavaService.getInstance(method.getProject()).findStatement(method);
        if (!ele.isPresent()||!(ele.get() instanceof Select)) {
            return Optional.empty();
        }
        Select select = (Select) ele.get();
        //找到方法的返回值对应的PsiClass
        Optional<PsiClass> target = AbstractStatementGenerator.getSelectResultType(method);
        PsiClass clazz = select.getResultType().getValue();
        PsiIdentifier ide = method.getNameIdentifier();
        if (null != ide && null == select.getResultMap().getValue()) {
            if (!target.isPresent() && null != clazz) {
                return Optional
                        .of(manager.createProblemDescriptor(ide, "返回值和SQL select id=\"#ref\" 返回值不匹配",
                                (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR, isOnTheFly));
            }
            if (target.isPresent() && (null == clazz || !target.get().equals(clazz))) {
                return Optional
                        .of(manager.createProblemDescriptor(ide, "返回值和SQL select id=\"#ref\" 返回值不匹配",
                                new ResultTypeQuickFix(select, target.get()), ProblemHighlightType.GENERIC_ERROR,
                                isOnTheFly));
            }
        }
        return Optional.empty();
    }

    private Optional<ProblemDescriptor> checkStatementExists(PsiMethod method, InspectionManager manager,
                                                                       boolean isOnTheFly) {
        PsiIdentifier ide = method.getNameIdentifier();
        if (!JavaService.getInstance(method.getProject()).findStatement(method).isPresent() && null != ide) {
            return Optional
                .of(manager.createProblemDescriptor(ide, "\"#ref\" 方法在XML配置文件中没有定义",
                    new StatementNotExistsQuickFix(method), ProblemHighlightType.GENERIC_ERROR, isOnTheFly));
        }
        return Optional.empty();
    }

}
