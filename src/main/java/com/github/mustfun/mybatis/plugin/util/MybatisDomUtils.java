package com.github.mustfun.mybatis.plugin.util;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * dom节点工具类
 * 操作mapper文件用， 跟mavenDomUtil类似
 */
public final class MybatisDomUtils {

    private MybatisDomUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @NonNls
    public static <T extends DomElement> Collection<T> findDomElements(@NotNull Project project, Class<T> clazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<T>> elements = DomService.getInstance().getFileElements(clazz, project, scope);
        //讲domFileElement转化为domElement
        return Collections2.transform(elements, new Function<DomFileElement<T>, T>() {
            @Override
            public T apply(DomFileElement<T> input) {
                return input.getRootElement();
            }
        });
    }

    /**
     * 是否是xml mapper file
     */
    public static boolean isMybatisFile(@Nullable PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("mapper");
    }

    public static boolean isMybatisConfigurationFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("configuration");
    }

    public static boolean isBeansFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return null != rootTag && rootTag.getName().equals("beans");
    }

    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }

}