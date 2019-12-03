package com.github.mustfun.mybatis.plugin.definitionsearch;

import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;


/**
 * @author yanglin
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement element, @NotNull Processor<? super XmlElement> consumer) {
        if (!(element instanceof PsiTypeParameterListOwner)) {
            return;
        }

        Processor<DomElement> processor = new Processor<DomElement>() {
            @Override
            public boolean process(DomElement domElement) {
                return consumer.process(domElement.getXmlElement());
            }
        };

        JavaService.getInstance(element.getProject()).process(element, processor);
    }
}
