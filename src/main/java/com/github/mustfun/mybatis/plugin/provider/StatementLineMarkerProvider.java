package com.github.mustfun.mybatis.plugin.provider;

import com.github.mustfun.mybatis.plugin.dom.model.Delete;
import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.dom.model.Insert;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.github.mustfun.mybatis.plugin.dom.model.Update;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @update itar
 * @function
 */
public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlTag, PsiNameIdentifierOwner> {

    private static final ImmutableList<Class<? extends GroupTwo>> TARGET_TYPES = ImmutableList.of(
        Select.class,
        Update.class,
        Insert.class,
        Delete.class
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlTag
            && MapperUtils.isElementWithinMybatisFile(element)
            && isTargetType(element);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Optional<PsiNameIdentifierOwner> apply(@NotNull XmlTag from) {
        Optional<PsiNameIdentifierOwner> optional = Optional.absent();
        DomElement domElement = DomUtil.getDomElement(from);
        //如果是Mapper
        if (domElement instanceof Mapper) {
            String namespace = ((Mapper) domElement).getNamespace().toString();
            Optional<PsiClass> clazz = JavaUtils.findClazz(from.getProject(), namespace);
            //有可能找不到
            if (clazz.isPresent()) {
                return Optional.of(clazz.get());
            }
        } else {
            if (null == domElement) {
                optional = Optional.absent();
            } else {
                Optional<PsiMethod> method = JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
                if (!method.isPresent()) {
                    return Optional.absent();
                }
                optional = Optional.of(method.get());
            }
        }
        return optional;
    }

    private boolean isTargetType(PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        for (Class<?> clazz : TARGET_TYPES) {
            if (clazz.isInstance(domElement)) {
                return true;
            }
        }
        if (domElement instanceof Mapper) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Navigatable getNavigatable(@NotNull XmlTag from, @NotNull PsiNameIdentifierOwner target) {
        return (Navigatable) target.getNavigationElement();
    }

    @NotNull
    @Override
    public String getTooltip(@NotNull XmlTag from, @NotNull PsiNameIdentifierOwner target) {
        return "Data access object found - ";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
