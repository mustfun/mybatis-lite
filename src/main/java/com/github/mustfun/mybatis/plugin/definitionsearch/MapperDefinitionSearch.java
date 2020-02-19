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
 * @function QueryExecutor接口的适配器，使编写实现更容易。
 * 它提供了一种将实现代码自动包装成读操作的可能性。
 * 在索引过程中，
 * 如果查询执行器没有实现com.intellij.openapi.project.DumbAware(但是需要在read操作中运行)，
 * 那么查询执行器就会被延迟，直到索引完成，
 * 因为搜索参数实现了DumbAwareSearchParameters。
 * 此外，processQuery(Object, Processor)不需要返回一个布尔值，
 * 因此很难停止整个搜索意外返回false。
 */

/**
 * 第一个参数是结果，第二个参数是参数
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    /**
     * 根据queryParameters找到一些结果并提供给消费者。 - 这里是element元素
     * 如果消费者返回false，则停止
     * @param element   搜索的param
     * @param consumer  搜索的result
     */
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
