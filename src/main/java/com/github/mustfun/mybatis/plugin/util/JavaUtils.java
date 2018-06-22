package com.github.mustfun.mybatis.plugin.util;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.model.enums.VmTypeEnums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;

import com.intellij.ui.CheckBoxList;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yanglin
 */
public final class JavaUtils {

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid();
    }

    @NotNull
    public static Optional<PsiField> findSettablePsiField(@NotNull PsiClass clazz, @Nullable String propertyName) {
        PsiMethod propertySetter = PropertyUtil.findPropertySetter(clazz, propertyName, false, true);
        return null == propertySetter ? Optional.<PsiField>absent() : Optional.fromNullable(PropertyUtil.findPropertyFieldByMember(propertySetter));
    }

    @NotNull
    public static PsiField[] findSettablePsiFields(@NotNull PsiClass clazz) {
        PsiMethod[] methods = clazz.getAllMethods();
        List<PsiField> fields = Lists.newArrayList();
        for (PsiMethod method : methods) {
            if (PropertyUtil.isSimplePropertySetter(method)) {
                Optional<PsiField> psiField = findSettablePsiField(clazz, PropertyUtil.getPropertyName(method));
                if (psiField.isPresent()) {
                    fields.add(psiField.get());
                }
            }
        }
        return fields.toArray(new PsiField[fields.size()]);
    }

    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.fromNullable(type).isPresent() && type.isInterface();
    }

    @NotNull
    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        return Optional.fromNullable(JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.allScope(project)));
    }

    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.absent();
        }
        Optional<PsiClass> clazz = findClazz(project, clazzName);
        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? Optional.<PsiMethod>absent() : Optional.of(methods[0]);
        }
        return Optional.absent();
    }

    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, MapperUtils.getNamespace(element), MapperUtils.getId(element));
    }

    public static boolean isAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null != modifierList && null != modifierList.findAnnotation(annotation.getQualifiedName());
    }

    @NotNull
    public static Optional<PsiAnnotation> getPsiAnnotation(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null == modifierList ? Optional.<PsiAnnotation>absent() : Optional.fromNullable(modifierList.findAnnotation(annotation.getQualifiedName()));
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationAttributeValue(@NotNull PsiModifierListOwner target,
                                                                                 @NotNull Annotation annotation,
                                                                                 @NotNull String attrName) {
        if (!isAnnotationPresent(target, annotation)) {
            return Optional.absent();
        }
        Optional<PsiAnnotation> psiAnnotation = getPsiAnnotation(target, annotation);
        return psiAnnotation.isPresent() ? Optional.fromNullable(psiAnnotation.get().findAttributeValue(attrName)) : Optional.<PsiAnnotationMemberValue>absent();
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationValue(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        return getAnnotationAttributeValue(target, annotation, "value");
    }

    public static Optional<String> getAnnotationValueText(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        Optional<PsiAnnotationMemberValue> annotationValue = getAnnotationValue(target, annotation);
        return annotationValue.isPresent() ? Optional.of(annotationValue.get().getText().replaceAll("\"", "")) : Optional.<String>absent();
    }

    public static boolean isAnyAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Set<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (isAnnotationPresent(target, annotation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllParameterWithAnnotation(@NotNull PsiMethod method, @NotNull Annotation annotation) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (PsiParameter parameter : parameters) {
            if (!isAnnotationPresent(parameter, annotation)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasImportClazz(@NotNull PsiJavaFile file, @NotNull String clazzName) {
        PsiImportList importList = file.getImportList();
        if (null == importList) {
            return false;
        }
        PsiImportStatement[] statements = importList.getImportStatements();
        for (PsiImportStatement tmp : statements) {
            if (null != tmp && tmp.getQualifiedName().equals(clazzName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 寻找离 public class最近的元素
     *
     * @param psiElement
     * @return
     */
    public static PsiElement findNealModifierElement(PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }
        if (psiElement instanceof PsiModifierList) {
            return psiElement;
        } else {
            return findNealModifierElement(psiElement.getNextSibling());
        }
    }

    /**
     * 找出Mapper等文件可能所在的路径所在路径
     * @param base
     * @param patten 多个进行匹配,或者关系, 如DemoDao.java或者DemoMapper.java都可以
     * @return
     */
    public static VirtualFile getFilePattenPath(VirtualFile base, String... patten){
        if (base.getPath().contains("/.git/")||base.getPath().contains("/.idea/")){
            return null;
        }
        for (String s : patten) {
            if (base.getPath().toUpperCase().contains(s.toUpperCase())){
                return base.getParent();
            }
        }
        if (base.getChildren().length!=0){
            for (VirtualFile virtualFile : base.getChildren()) {
                //这个地方不应该直接return，存在多个文件夹的情况
                VirtualFile filePattenPath = getFilePattenPath(virtualFile, patten);
                if (filePattenPath!=null){
                    return filePattenPath;
                }else{
                    continue;
                }
            }
        }
        return null;
    }



    public static List collectSelectedCheckBox(CheckBoxList checkBoxList) {
        List list = new ArrayList<>();
        for (int i = 0; i < checkBoxList.getItemsCount(); i++) {
            Object itemAt = checkBoxList.getItemAt(i);
            if (checkBoxList.isItemSelected(itemAt)) {
                list.add(itemAt);
            }
        }
        return list;
    }

    public static String getPackageName(PsiDirectory psiDirectory,Integer vmType){
        PsiFile[] files = psiDirectory.getFiles();
        if (files.length!=0){
            if (!(files[0] instanceof PsiJavaFile)){
                return "com.github.mustfun";
            }
            PsiJavaFile file = (PsiJavaFile) files[0];
            return file.getPackageName();
        }
        //如果下面没有文件,就上一层找，暂时不考虑新建很多层那种复杂情况
        PsiDirectory parent = psiDirectory.getParent();
        PsiFile[] pare = parent.getFiles();
        if (pare.length!=0){
            if (!(files[0] instanceof PsiJavaFile)){
                return "com.github.mustfun";
            }
            PsiJavaFile file = (PsiJavaFile) files[0];
            return file.getPackageName()+getClassType(vmType);
        }
        return "com.github.mustfun";
    }

    public static String getClassType(Integer template){
        if (template.equals(VmTypeEnums.MODEL_PO.getCode())) {
            return  "/po";
        }

        if (template.equals(VmTypeEnums.MODEL_BO.getCode())) {
            return  "/bo";
        }

        if (template.equals(VmTypeEnums.MODEL_REQ.getCode())) {
            return  "/req";
        }

        if (template.equals(VmTypeEnums.MODEL_RESP.getCode())) {
            return  "/resp";
        }

        if (template.equals(VmTypeEnums.SERVICE_IMPL.getCode())) {
            return  "/impl";
        }
        return null;
    }

    /**
     * 通过类名找一个类
     * @return
     */
    // FIXME: 2018/6/22
    public PsiClass[] findClassByName(){
        return PsiShortNamesCache.getInstance(null).getClassesByName(null,null);
    }
}
