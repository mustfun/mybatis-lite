package com.github.mustfun.mybatis.plugin.alias;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @funciton 别名类跳转，比如int直接可以跳转到Integer源码
 * @see com.intellij.psi.PsiReference PsiReference: 对PSI元素的引用。例如，表达式中使用的变量名,可以使用“Go to Declaration”操作从引用转到它引用的元素
 */
public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {

    private Function<AliasDesc, String> function = new Function<AliasDesc, String>() {
        @Override
        public String apply(AliasDesc input) {
            return input.getAlias();
        }
    };

    public AliasClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XmlAttributeValue attributeValue = getElement();
        //把 long ,int 这种转化为psiElement
        return AliasFacade.getInstance(attributeValue.getProject())
            .findPsiClass(attributeValue, attributeValue.getValue()).orNull();
    }

    /**
     * 返回字符串、PsiElement和或com.intellij.codeInsight.lookup.LookupElement的数组表示在引用位置可见的所有标识符的实例。
     * 内容用于构建用于基本代码完成的查找列表。(列表的可见标识符可能不会被完成前缀字符串过滤过滤稍后由IDE执行。)
     * @return
     * 这里返回了别名的所有数组， list , long, int 等候选
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        AliasFacade aliasFacade = AliasFacade.getInstance(getElement().getProject());
        Collection<String> result = Collections2.transform(aliasFacade.getAliasDescs(getElement()), function);
        return result.toArray(new String[result.size()]);
    }

}
