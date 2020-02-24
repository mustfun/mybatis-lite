package com.github.mustfun.mybatis.plugin.util;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
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

import java.util.*;

/**
 * @author yanglin
 */
public final class JavaUtils {

    public static final String DEFAULT_PACKAGE_NAME = "com.github.mustfun";

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid();
    }

    @NotNull
    public static Optional<PsiField> findSettablePsiField(@NotNull PsiClass clazz, @Nullable String propertyName) {
        PsiMethod propertySetter = PropertyUtil.findPropertySetter(clazz, propertyName, false, true);
        return null == propertySetter ? Optional.<PsiField>empty()
            : Optional.ofNullable(PropertyUtil.findPropertyFieldByMember(propertySetter));
    }

    @NotNull
    public static PsiField[] findSettablePsiFields(@NotNull PsiClass clazz) {
        PsiMethod[] methods = clazz.getAllMethods();
        List<PsiField> fields = Lists.newArrayList();
        for (PsiMethod method : methods) {
            if (PropertyUtil.isSimplePropertySetter(method)) {
                Optional<PsiField> psiField = findSettablePsiField(clazz, PropertyUtil.getPropertyName(method));
                psiField.ifPresent(fields::add);
            }
        }
        return fields.toArray(new PsiField[0]);
    }

    /**
     * 判断本身是不是接口，否则看元素的父类是不是一个接口
     * @param element
     * @return
     */
    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }

    @NotNull
    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        //找到一个类
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.allScope(project)));
    }

    /**
     * 通过全限定名找到PSiClass,通过findMethodsByName找到相关的PSIMethod
     * @param project
     * @param clazzName
     * @param methodName
     * @return
     */
    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @Nullable String clazzName,
                                                           @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass> clazz = findClazz(project, Objects.requireNonNull(clazzName));
        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? Optional.<PsiMethod>empty() : Optional.of(methods[0]);
        }
        return Optional.empty();
    }

    /**
     * 从xml里面的id来获取对应的方法
     * @param project
     * @param element
     * @return
     */
    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, MapperUtils.getNamespace(element), MapperUtils.getId(element));
    }

    /**
     * 判断修饰符是否跟@Param相等 ， 注解也是一种修饰符
     * @param target
     * @param annotation
     * @return
     */
    public static boolean isAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null != modifierList && null != modifierList.findAnnotation(annotation.getQualifiedName());
    }

    @NotNull
    public static Optional<PsiAnnotation> getPsiAnnotation(@NotNull PsiModifierListOwner target,
                                                                     @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null == modifierList ? Optional.<PsiAnnotation>empty()
            : Optional.ofNullable(modifierList.findAnnotation(annotation.getQualifiedName()));
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationAttributeValue(@NotNull PsiModifierListOwner target,
                                                                                           @NotNull Annotation annotation,
                                                                                           @NotNull String attrName) {
        if (!isAnnotationPresent(target, annotation)) {
            return Optional.empty();
        }
        Optional<PsiAnnotation> psiAnnotation = getPsiAnnotation(target, annotation);
        //是否为空，不为空就找出@param 注解 value属性
        return psiAnnotation.map(value -> value.findAttributeValue(attrName));
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationValue(@NotNull PsiModifierListOwner target,
                                                                                  @NotNull Annotation annotation) {
        return getAnnotationAttributeValue(target, annotation, "value");
    }

    public static Optional<String> getAnnotationValueText(@NotNull PsiModifierListOwner target,
                                                                    @NotNull Annotation annotation) {
        Optional<PsiAnnotationMemberValue> annotationValue = getAnnotationValue(target, annotation);
        return annotationValue.map(psiAnnotationMemberValue -> psiAnnotationMemberValue.getText().replaceAll("\"", ""));
    }

    public static boolean isAnyAnnotationPresent(@NotNull PsiModifierListOwner target,
        @NotNull Set<Annotation> annotations) {
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
     *
     * @param patten 多个进行匹配,或者关系, 如DemoDao.java或者DemoMapper.java都可以
     */
    public static VirtualFile getFilePattenPath(VirtualFile base, String... patten) {
        if (base.getPath().contains("/.git/") || base.getPath().contains("/.idea/")) {
            return null;
        }
        for (String s : patten) {
            if (base.getPath().toUpperCase().contains(s.toUpperCase())) {
                return base.getParent();
            }
        }
        if (base.getChildren().length != 0) {
            for (VirtualFile virtualFile : base.getChildren()) {
                //这个地方不应该直接return，存在多个文件夹的情况
                VirtualFile filePattenPath = getFilePattenPath(virtualFile, patten);
                if (filePattenPath != null) {
                    return filePattenPath;
                } else {
                    continue;
                }
            }
        }
        return null;
    }


    /**
     * 根据文件名找文件路径 - 模糊匹配
     */
    public static VirtualFile getFileByPattenName(VirtualFile base, String... patten) {
        if (base.getPath().contains("/.git/") || base.getPath().contains("/.idea/")
            || base.getPath().contains("/.target/")) {
            return null;
        }
        for (String s : patten) {
            if (base.getPath().toUpperCase().contains(s.toUpperCase())) {
                return base;
            }
        }
        if (base.getChildren().length != 0) {
            for (VirtualFile virtualFile : base.getChildren()) {
                //这个地方不应该直接return，存在多个文件夹的情况
                VirtualFile filePattenPath = getFileByPattenName(virtualFile, patten);
                if (filePattenPath != null) {
                    return filePattenPath;
                } else {
                    continue;
                }
            }
        }
        return null;
    }


    /**
     * 通过文件名找到文件路径 - 文件存在的情况
     * @param base
     * @param fileName
     * @return
     */
    public static VirtualFile getExistFilePathByName(VirtualFile base, String fileName) {
        if (base.getPath().contains("/.git/") || base.getPath().contains("/.idea/")
                || base.getPath().contains("/.target/")) {
            return null;
        }
        //本层目录是否包含
        if (base.getPath().toUpperCase().contains(fileName.toUpperCase())) {
            return base;
        }
        if (base.getChildren().length != 0) {
            for (VirtualFile virtualFile : base.getChildren()) {
                //这个地方不应该直接return，存在多个文件夹的情况
                VirtualFile filePattenPath = getExistFilePathByName(virtualFile, fileName);
                if (filePattenPath != null) {
                    return filePattenPath;
                } else {
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


    /**
     * 从虚拟文件获取全限定名
     * @param project
     * @param virtualFile
     * @return
     */
    public static String getFullClassPath(Project project,VirtualFile virtualFile,String className){
        return getPackageName(project, virtualFile)+ "." + className.split("\\.")[0];
    }

    /**
     * 从虚拟文件获取包名
     * @param project
     * @param virtualFile
     * @return
     */
    public static String getPackageName(Project project,VirtualFile virtualFile){
        if (virtualFile==null){
            return DEFAULT_PACKAGE_NAME;
        }
        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        if (file==null){
            return DEFAULT_PACKAGE_NAME;
        }
        return getPackageName(file);
    }

    /**
     * 从psiFile获取包名
     * @param file
     * @return
     */
    public static String getPackageName(PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return DEFAULT_PACKAGE_NAME;
        }
        PsiJavaFile javaFile = (PsiJavaFile) file;
        return javaFile.getPackageName();
    }


    /**
     * 只有一个空文件夹向上递归确定包名 - 适用于文件一定存在的情况
     * @param psiDirectory
     * @return
     */
    public static String getPackageNameForExistFile(PsiDirectory psiDirectory,StringBuilder stringBuilder) {
        if (psiDirectory==null){
            return DEFAULT_PACKAGE_NAME;
        }
        PsiFile[] files = psiDirectory.getFiles();
        for (PsiFile file : files) {
            if (!(file instanceof PsiJavaFile)) {
                continue;
            }
            PsiJavaFile javaFile = (PsiJavaFile) files[0];
            return javaFile.getPackageName()+stringBuilder.toString();
        }
        //如果下面没有文件,就上一层找
        return getPackageNameForExistFile(psiDirectory.getParent(),stringBuilder.append("\\.").append(psiDirectory.getName()));
    }

    /**
     * 从当前文件夹获取包名 - 不存在的文件夹
     * @param virtualFile
     * @return
     */
    public static String getNotExistPackageNameFromDirectory(VirtualFile virtualFile) {
        return virtualFile.getPath().split("src/main/java/")[1].replace("/",".");
    }

    /**
     * 获取所有的方法，根据nameSpace
     * @param project
     * @param namespace
     * @return
     */
    public static Collection<? extends PsiMethod> findMethods(Project project, String namespace) {
        if (StringUtils.isBlank(namespace)) {
            return Collections.emptyList();
        }
        Optional<PsiClass> clazz = findClazz(project, Objects.requireNonNull(namespace));
        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().getMethods();
            return Arrays.asList(methods);
        }
        return Collections.emptyList();
    }


    /**
     * 通过类名找一个类
     */
    // FIXME: 2018/6/22
    public PsiClass[] findClassByName() {
        return PsiShortNamesCache.getInstance(null).getClassesByName(null, null);
    }

    /**
     * 在yaml中遍历一个节点
     */
    public static String findYamlValueByTag(LinkedHashMap hashMap, String... keywords) {
        for (Object o : hashMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            if (val instanceof LinkedHashMap) {
                String yamlValueByTag = findYamlValueByTag((LinkedHashMap) val, keywords);
                if (yamlValueByTag == null) {
                    continue;
                } else {
                    return yamlValueByTag;
                }
            } else {
                for (String keyword : keywords) {
                    if (key.equals(keyword)) {
                        return (String) val;
                    }
                }
            }
        }
        return null;
    }
}
