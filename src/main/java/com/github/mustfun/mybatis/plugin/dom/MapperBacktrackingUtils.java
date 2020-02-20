package com.github.mustfun.mybatis.plugin.dom;

import com.github.mustfun.mybatis.plugin.dom.model.Association;
import com.github.mustfun.mybatis.plugin.dom.model.Collection;
import com.github.mustfun.mybatis.plugin.dom.model.ParameterMap;
import com.github.mustfun.mybatis.plugin.dom.model.ResultMap;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @author yanglin
 */
public final class MapperBacktrackingUtils {

    private MapperBacktrackingUtils() {
        throw new UnsupportedOperationException();
    }

    public static Optional<PsiClass> getPropertyClazz(PsiElement attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return java.util.Optional.empty();
        }

        Collection collection = DomUtil.getParentOfType(domElement, Collection.class, true);
        if (null != collection && !isWithinSameTag(collection, attributeValue)) {
            return java.util.Optional.ofNullable(collection.getOfType().getValue());
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            return java.util.Optional.ofNullable(association.getJavaType().getValue());
        }

        ParameterMap parameterMap = DomUtil.getParentOfType(domElement, ParameterMap.class, true);
        if (null != parameterMap && !isWithinSameTag(parameterMap, attributeValue)) {
            return java.util.Optional.ofNullable(parameterMap.getType().getValue());
        }

        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return java.util.Optional.ofNullable(resultMap.getType().getValue());
        }
        return java.util.Optional.empty();
    }

    public static boolean isWithinSameTag(@NotNull DomElement domElement, @NotNull PsiElement xmlElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(xmlElement, XmlTag.class);
        return Objects.equals(domElement.getXmlTag(), xmlTag);
    }

}
