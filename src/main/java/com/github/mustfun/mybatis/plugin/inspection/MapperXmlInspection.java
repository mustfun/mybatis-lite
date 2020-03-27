package com.github.mustfun.mybatis.plugin.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;

/**
 * @author yanglin
 * @updater itar
 * @function xml里面的语法校验
 */
public class MapperXmlInspection extends BasicDomElementsInspection<DomElement> {

    public MapperXmlInspection() {
        super(DomElement.class);
    }

    /**
     * 会对xml的value进行校验 , 底层会调用resolve方法校验value是否是一个psiElement
     * 首先会用element.getValue方法得到converter之后的值，如<select id=""></select> 就拿到的是一个PsiMethod，那么就不会报错了，高级
     * @param element
     * @param holder
     * @param helper
     * 还会对required注解进行校验
     */
    @Override
    protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder,
        DomHighlightingHelper helper) {
        super.checkDomElement(element, holder, helper);
    }

}
