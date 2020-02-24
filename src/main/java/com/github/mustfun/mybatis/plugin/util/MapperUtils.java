package com.github.mustfun.mybatis.plugin.util;

import com.github.mustfun.mybatis.plugin.dom.model.Package;
import com.github.mustfun.mybatis.plugin.dom.model.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Collection;


/**
 * @author yanglin
 * @updater itar
 * @function Mapper的一些util
 */
public final class MapperUtils {

    private MapperUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 传进来的element如果是IdDomElement，那么直接传出，如果不是，就找他的parent
     * @param element
     * @return
     */
    @NotNull
    public static Optional<IdDomElement> findParentIdDomElement(@Nullable PsiElement element) {
        //PSI Element转化为domElement
        DomElement domElement = DomUtil.getDomElement(element);
        if (null == domElement) {
            return Optional.empty();
        }
        if (domElement instanceof IdDomElement) {
            return Optional.of((IdDomElement) domElement);
        }
        return Optional.ofNullable(DomUtil.getParentOfType(domElement, IdDomElement.class, true));
    }

    public static PsiElement createMapperFromFileTemplate(@NotNull String fileTemplateName,
        @NotNull String fileName,
        @NotNull PsiDirectory directory,
        @Nullable Properties pops) throws Exception {
        FileTemplate fileTemplate = FileTemplateManager.getInstance(directory.getProject()).getJ2eeTemplate(fileTemplateName);
        return FileTemplateUtil.createFromTemplate(fileTemplate, fileName, pops, directory);
    }

    @NotNull
    public static Collection<PsiDirectory> findMapperDirectories(@NotNull Project project) {
        return Collections2.transform(findMappers(project), new Function<Mapper, PsiDirectory>() {
            @Override
            public PsiDirectory apply(Mapper input) {
                return input.getXmlElement().getContainingFile().getContainingDirectory();
            }
        });
    }

    public static boolean isElementWithinMybatisFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && MybatisDomUtils.isMybatisFile(psiFile);
    }

    /**
     * 找到所有的mapper
     * @param project
     * @return
     */
    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project) {
        return MybatisDomUtils.findDomElements(project, Mapper.class);
    }

    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull String namespace) {
        List<Mapper> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            if (getNamespace(mapper).equals(namespace)) {
                result.add(mapper);
            }
        }
        return result;
    }

    @NotNull
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull PsiClass clazz) {
        return JavaUtils.isElementWithinInterface(clazz) ? findMappers(project, clazz.getQualifiedName())
            : Collections.<Mapper>emptyList();
    }

    @NotNull
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull PsiMethod method) {
        PsiClass clazz = method.getContainingClass();
        return null == clazz ? Collections.<Mapper>emptyList() : findMappers(project, clazz);
    }

    @NotNull
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull String namespace) {
        Collection<Mapper> mappers = findMappers(project, namespace);
        return CollectionUtils.isEmpty(mappers) ? Optional.<Mapper>empty() : Optional.of(mappers.iterator().next());
    }

    @NotNull
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull PsiClass clazz) {
        String qualifiedName = clazz.getQualifiedName();
        return null != qualifiedName ? findFirstMapper(project, qualifiedName) : Optional.empty();
    }

    @NotNull
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull PsiMethod method) {
        PsiClass containingClass = method.getContainingClass();
        return null != containingClass ? findFirstMapper(project, containingClass) :  Optional.empty();
    }

    /**
     * 从domElement查找父节点对象 - domElement一般是GeneticAttributeValue对象，如<resultMap extends='BaseResultMap'></resultMap>
     * 那么拿到的就是resultMap这个父节点
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @NonNls
    public static Mapper getMapper(@NotNull DomElement element) {
        Optional<Mapper> optional = Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull Mapper mapper) {
        String ns = mapper.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull DomElement element) {
        return getNamespace(getMapper(element));
    }

    @NonNls
    public static boolean isMapperWithSameNamespace(@Nullable Mapper mapper, @Nullable Mapper target) {
        return null != mapper && null != target && getNamespace(mapper).equals(getNamespace(target));
    }

    @Nullable
    @NonNls
    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    /**
     * 获得id签名，全限定名+id
     * @param domElement
     * @param <T>
     * @return
     */
    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement) {
        return getNamespace(domElement) + "." + getId(domElement);
    }

    /**
     * 获得id签名，全限定名+id,如果第一个的签名和第二个的签名是一样的情况，就返回短一点的id
     * 其实本签名就是返回id
     * @param domElement
     * @param <T>
     * @return
     */
    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement, @NotNull Mapper mapper) {
        Mapper contextMapper = getMapper(domElement);
        String id = getId(domElement);
        if (id == null) {
            id = "";
        }
        String idsignature = getIdSignature(domElement);
        //getIdSignature(domElement)
        return isMapperWithSameNamespace(contextMapper, mapper) ? id : idsignature;
    }

    public static void processConfiguredTypeAliases(@NotNull Project project, @NotNull Processor<TypeAlias> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (TypeAlias ta : tas.getTypeAlias()) {
                    String stringValue = ta.getAlias().getStringValue();
                    if (null != stringValue && !processor.process(ta)) {
                        return;
                    }
                }
            }
        }
    }

    private static Collection<Configuration> getMybatisConfigurations(Project project) {
        return MybatisDomUtils.findDomElements(project, Configuration.class);
    }

    public static void processConfiguredPackage(@NotNull Project project,
        @NotNull Processor<Package> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (Package pkg : tas.getPackages()) {
                    if (!processor.process(pkg)) {
                        return;
                    }
                }
            }
        }
    }
}
