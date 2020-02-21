package com.github.mustfun.mybatis.plugin.provider;

import com.github.mustfun.mybatis.plugin.dom.model.*;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yanglin
 * @update itar
 * @function xml跳转到java里面，找到具体的method
 */
public class StatementLineAbstractMarkerProvider extends AbstractSimpleLineAbstractMarkerProvider<XmlTag, PsiNameIdentifierOwner> {

    private static final List<Class<? extends GroupTwo>> TARGET_TYPES = Collections.unmodifiableList(
            Arrays.asList(Select.class,
                    Update.class,
                    Insert.class,
                    Delete.class)
    );

    public static final List<String> TARGET_TOKEN = Collections.unmodifiableList(Arrays.asList("select", "update", "insert", "delete","mapper"));
    public static final String XML_START_TAG_START = "<";

    /**
     * 系统推荐检测元素尽量的小，这里做一个小调整，增强性能
     * 用token来校验
     * @param element
     * @return
     */
    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken && isTargetToken(element) && element.getParent() instanceof XmlTag
            && MapperUtils.isElementWithinMybatisFile(element.getParent())
            && isTargetType(element.getParent());
    }

    private boolean isTargetToken(PsiElement element) {
        return TARGET_TOKEN.contains(element.getText()) &&
                element.getPrevSibling().getText().equalsIgnoreCase(XML_START_TAG_START);
    }

    @NotNull
    @Override
    public Optional<PsiNameIdentifierOwner> apply(@NotNull XmlTag from) {
        Optional<PsiNameIdentifierOwner> optional = Optional.empty();
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
                optional = Optional.empty();
            } else {
                Optional<PsiMethod> method = JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
                if (!method.isPresent()) {
                    return Optional.empty();
                }
                optional = Optional.of(method.get());
            }
        }
        return optional;
    }

    /**
     * 检查XMLTag是否是4种类型
     * @param element
     * @return
     */
    private boolean isTargetType(PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        for (Class<?> clazz : TARGET_TYPES) {
            if (clazz.isInstance(domElement)) {
                return true;
            }
        }
        if (domElement instanceof Mapper) {
            XmlTagImpl xmlTag = (XmlTagImpl) element;
            if (!"mapper".equalsIgnoreCase(xmlTag.getName())){
                return false;
            }
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Navigatable getNavigatable(@NotNull XmlTag from, @NotNull PsiNameIdentifierOwner target) {
        return (Navigatable) target.getNavigationElement();
    }

    @NotNull
    @Override
    public String getTooltip(@NotNull XmlTag from, @NotNull PsiNameIdentifierOwner target) {
        return "跳转到相应JAVA方法";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
