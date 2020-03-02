package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.*;
import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.jam.model.util.JamCommonUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.spring.SpringLibraryUtil;
import com.intellij.spring.java.SpringJavaClassInfo;
import com.intellij.spring.model.jam.JamSpringBeanPointer;
import com.intellij.spring.model.jam.javaConfig.ContextJavaBean;
import com.intellij.spring.model.utils.SpringAutowireUtil;
import com.intellij.spring.model.utils.SpringCommonUtils;
import com.intellij.spring.model.utils.SpringModelUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 修复autowired的一个类
 * @author itar
 * @date 2020-03-02
 */

public class SpringJavaAutowiredFieldsFixInspection extends AbstractBaseJavaLocalInspectionTool {

    @Nullable
    @Override
    public ProblemDescriptor[] checkClass(@NotNull PsiClass psiClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        PsiFile file = psiClass.getContainingFile();
        if (JamCommonUtil.isPlainJavaFile(file) && !ProjectRootsUtil.isInTestSource(file)) {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiClass);
            if (!SpringCommonUtils.hasSpringFacet(module) && !SpringModelUtils.getInstance().hasAutoConfiguredModels(module)) {
                return ProblemDescriptor.EMPTY_ARRAY;
            } else if (!SpringCommonUtils.isStereotypeComponentOrMeta(psiClass)) {
                return ProblemDescriptor.EMPTY_ARRAY;
            } else if (!SpringLibraryUtil.isAtLeastVersion(module, SpringLibraryUtil.SpringVersion.V_4_3)) {
                return ProblemDescriptor.EMPTY_ARRAY;
            }else{
                List<JamSpringBeanPointer> stereotypeMappedBeans = SpringJavaClassInfo.getSpringJavaClassInfo(psiClass).getStereotypeMappedBeans();
                for (JamSpringBeanPointer stereotypeMappedBean : stereotypeMappedBeans) {
                    if (stereotypeMappedBean.getSpringBean() instanceof ContextJavaBean){
                        break;
                    }
                    ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
                    Set<String> annotations = SpringAutowireUtil.getAutowiredAnnotations(module);
                    //获取自动注入的字段
                    Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> autowiredFields = getAutowiredFields(psiClass, annotations);
                    LocalQuickFix allFieldsFix = null;
                    if (autowiredFields.size() > 1) {
                        allFieldsFix = getFieldsAutowiredFix(psiClass, autowiredFields, annotations);
                    }
                    for (Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>> autowiredField : autowiredFields) {
                        LocalQuickFix fieldFx = getFieldsAutowiredFix(psiClass, Collections.singleton(autowiredField), annotations);
                        LocalQuickFix[] fixes = allFieldsFix != null ? new LocalQuickFix[]{fieldFx, allFieldsFix} : new LocalQuickFix[]{fieldFx};
                        PsiAnnotation psiAnnotation = (PsiAnnotation)((SmartPsiElementPointer)autowiredField.getSecond()).getElement();
                        if (psiAnnotation != null) {
                            holder.registerProblem(psiAnnotation, "字段注入不推荐使用@Autowired或@Inject", fixes);
                        }
                    }
                    return holder.getResultsArray();
                }

            }
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private static LocalQuickFix getFieldsAutowiredFix(PsiClass psiClass, Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> fields, Set<String> annotations) {
        PsiMethod constructor = findAutowiredConstructor(psiClass, annotations);
        return (LocalQuickFix)(constructor != null ? addParameterQuickFix(psiClass, constructor, fields) : createAutowiredConstructorQuickFix(psiClass, fields));
    }


    private static Object createAutowiredConstructorQuickFix(PsiClass psiClass, Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> fields) {
        return null;
    }

    /**
     *
     * @param psiClass
     * @param constructor
     * @param fields
     * @return
     */
    private static Object addParameterQuickFix(PsiClass psiClass, PsiMethod constructor, Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> fields) {
        return null;
    }


    @Nullable
    private static PsiMethod findAutowiredConstructor(@NotNull PsiClass psiClass, @NotNull Set<String> annotations) {
        PsiMethod[] constructors = psiClass.getConstructors();
        if (constructors.length == 1) {
            return constructors[0] instanceof LightElement ? null : constructors[0];
        } else {
            for (PsiMethod method : constructors) {
                if (!(method instanceof LightElement) && AnnotationUtil.isAnnotated(method, annotations, 0)) {
                    return method;
                }
            }
            return null;
        }
    }

    /**
     * 获取自动注入的字段
     * @param psiClass
     * @param annotations
     * @return 左边是字段引用，右边是注解
     */
    private static Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> getAutowiredFields(@NotNull PsiClass psiClass, @NotNull Set<String> annotations) {


        Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> pairs = new HashSet<>();
        SmartPointerManager smartPointerManager = SmartPointerManager.getInstance(psiClass.getProject());
        PsiField[] psiFields = psiClass.getFields();
        for (PsiField psiField : psiFields) {
            for (String autowiredAnno : annotations) {
                //Inject或者Autowired的时候添加 ， Resouce不检测
                if (!autowiredAnno.equals("javax.annotation.Resource")) {
                    PsiAnnotation annotation = AnnotationUtil.findAnnotation(psiField, autowiredAnno);
                    if (annotation != null) {
                        smartPointerManager.createSmartPsiElementPointer(psiField);
                        pairs.add(Pair.create(smartPointerManager.createSmartPsiElementPointer(psiField), smartPointerManager.createSmartPsiElementPointer(annotation)));
                    }
                }
            }
        }


        return pairs;
    }


    /**
     * 两个fix类


    private static class AddParameterQuickFix implements LocalQuickFix {
        private final SmartPsiElementPointer<PsiMethod> myConstructor;
        private final Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> myParameterCandidates;

        AddParameterQuickFix(PsiClass containingClass, @NotNull PsiMethod constructor, @NotNull Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> pairs) {
            super();
            this.myConstructor = SmartPointerManager.getInstance(constructor.getProject()).createSmartPsiElementPointer(constructor);
            this.myParameterCandidates = pairs;
        }

        @Override
        @Nls
        @NotNull
        public String getName() {
            String var10000 = SpringBundle.message("field.injection.add.parameters", new Object[]{SpringJavaAutowiredFieldsWarningInspection.getConstructorName((PsiMethod)this.myConstructor.getElement(), this.myParameterCandidates)});
            return var10000;
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            if (project == null) {
                $$$reportNull$$$0(3);
            }

            if (descriptor == null) {
                $$$reportNull$$$0(4);
            }

            PsiMethod constructor = (PsiMethod)this.myConstructor.getElement();
            if (constructor != null) {
                if (!this.myParameterCandidates.isEmpty()) {
                    SpringJavaAutowiredFieldsWarningInspection.addParameters(this.myParameterCandidates, constructor);
                }

            }
        }

        @Nls
        @NotNull
        public String getFamilyName() {
            String var10000 = SpringBundle.message("field.injection.add.parameter.family.warning", new Object[0]);
            if (var10000 == null) {
                $$$reportNull$$$0(5);
            }

            return var10000;
        }
    }

    private static class CreateAutowiredConstructorQuickFix implements LocalQuickFix {
        private final SmartPsiElementPointer<PsiClass> myClass;
        private final Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> myParameterCandidates;
        private final String message;

        CreateAutowiredConstructorQuickFix(PsiClass containingClass, @NotNull Set<Pair<SmartPsiElementPointer<PsiField>, SmartPsiElementPointer<PsiAnnotation>>> pairs) {
            if (pairs == null) {
                $$$reportNull$$$0(0);
            }

            super();
            this.myClass = SmartPointerManager.getInstance(containingClass.getProject()).createSmartPsiElementPointer(containingClass);
            this.myParameterCandidates = pairs;
            this.message = SpringJavaAutowiredFieldsWarningInspection.getConstructorName(containingClass.getName(), this.myParameterCandidates);
        }

        @Nls
        @NotNull
        public String getName() {
            String var10000 = SpringBundle.message("field.injection.create.constructor.injection", new Object[]{this.message});
            if (var10000 == null) {
                $$$reportNull$$$0(1);
            }

            return var10000;
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            if (project == null) {
                $$$reportNull$$$0(2);
            }

            if (descriptor == null) {
                $$$reportNull$$$0(3);
            }

            PsiClass containingClass = (PsiClass)this.myClass.getElement();
            if (containingClass != null) {
                PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiMethod constructor = elementFactory.createConstructor();
                constructor.setName(containingClass.getName());
                PsiMethod addedConstructor = (PsiMethod)containingClass.add(constructor);
                PsiUtil.setModifierProperty(addedConstructor, "public", true);
                SpringJavaAutowiredFieldsWarningInspection.addParameters(this.myParameterCandidates, addedConstructor);
                SpringJavaAutowiredFieldsWarningInspection.addAutowiredAnnotationIfNeeded(containingClass, addedConstructor, SpringJavaAutowiredFieldsWarningInspection.getAutowiredAnnotation((SmartPsiElementPointer)((Pair)this.myParameterCandidates.iterator().next()).second));
            }
        }

        @Nls
        @NotNull
        public String getFamilyName() {
            String var10000 = SpringBundle.message("field.injection.create.constructor.family.warning", new Object[0]);
            if (var10000 == null) {
                $$$reportNull$$$0(4);
            }

            return var10000;
        }
    }
     */
}
