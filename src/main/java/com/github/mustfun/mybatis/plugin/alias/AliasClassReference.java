package com.github.mustfun.mybatis.plugin.alias;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yanglin
 * @updater itar
 * @funciton 别名类跳转，比如int直接可以跳转到Integer源码
 * @see PsiReference PsiReference: 对PSI元素的引用。例如，表达式中使用的变量名,可以使用“Go to Declaration”操作从引用转到它引用的元素
 */
public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {


    public AliasClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XmlAttributeValue attributeValue = getElement();
        //把 long ,int 这种转化为psiElement
        return AliasFacade.getInstance(attributeValue.getProject())
                .findPsiClass(attributeValue, attributeValue.getValue()).orElse(null);
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
        List<LookupElement> result = new ArrayList<>();
        AliasFacade aliasFacade = AliasFacade.getInstance(getElement().getProject());
        Collection<AliasDesc> aliasDescs = aliasFacade.getAliasDescs(getElement());
        for (AliasDesc aliasDesc : aliasDescs) {
            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.createWithIcon(aliasDesc.getClazz()).withTypeText(aliasDesc.getClazz().getQualifiedName());
            result.add(lookupElementBuilder);
        }
        return result.toArray(new LookupElement[0]);
    }
}
