package com.github.mustfun.mybatis.plugin.contributor;

import com.github.mustfun.mybatis.plugin.alias.AliasDesc;
import com.github.mustfun.mybatis.plugin.alias.AliasResolverFactory;
import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author yanglin
 * @update itar
 * @function xml中的代码补全
 */
public class XmlParamContributor extends CompletionContributor {

    /**
     * 指定代码补全的位置，xmlAttributeValue里面的xmlAttribute的name叫test的
     * XmlPatterns.psiElement()
     *                 .inside(XmlPatterns.xmlAttributeValue().inside(XmlPatterns.xmlAttribute().withName("test"))
     * 事实上这样也是足够的： XmlPatterns.psiElement().XmlPatterns.xmlAttribute().withName("test1")
     */
    public XmlParamContributor() {
        extend(CompletionType.BASIC,
            //指定代码补全的位置，xmlAttributeValue里面的xmlAttribute的name叫test的
            XmlPatterns.psiElement()
                .inside(XmlPatterns.xmlTag()).inside(XmlPatterns.xmlText()),
                getProvider());
    }

    @NotNull
    private CompletionProvider<CompletionParameters>  getProvider() {
        return new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                PsiElement position = parameters.getPosition();
                addElementForPsiParameter(position.getProject(), result,
                        MapperUtils.findParentIdDomElement(position).orElse(null));
            }
        };
    }

    /**
     * 给传进来的参数添加候选项
     * @param project
     * @param result
     * @param element
     */
    public static void addElementForPsiParameter(@NotNull Project project, @NotNull CompletionResultSet result,
                                          @Nullable IdDomElement element) {
        if (null == element) {
            return;
        }
        Optional<PsiMethod> method = JavaUtils.findMethod(project, element);
        if (!method.isPresent()){
            return ;
        }
        //拿到所有的parameter
        PsiParameter[] parameters = method.get().getParameterList().getParameters();
        for (PsiParameter parameter : parameters) {
            //拿到method上面@param上的value的值
            Optional<String> valueText = JavaUtils.getAnnotationValueText(parameter, Annotation.PARAM);
            if (valueText.isPresent()) {
                LookupElementBuilder builder = LookupElementBuilder.create(valueText.get())
                    .withIcon(Icons.PARAM_COMPLETION_ICON);
                //变成一个有优先级的对象，其实不加也行
                result.addElement(PrioritizedLookupElement.withPriority(builder, MybatisConstants.PRIORITY));
            }else{
                //参数为简单类型的时候 - String
                PsiClassReferenceType type = (PsiClassReferenceType) parameter.getType();
                if (type.hasParameters()){
                    type = (PsiClassReferenceType)type.getParameters()[0];
                }
                String name = type.getName();
                AliasDesc classAliasByName = AliasResolverFactory.createInnerAliasResolver(project).getClassAliasByName(name);
                if (classAliasByName!=null){
                    LookupElementBuilder builder = LookupElementBuilder.create(parameter.getName())
                            .withIcon(Icons.PARAM_COMPLETION_ICON);
                    //变成一个有优先级的对象，其实不加也行
                    result.addElement(PrioritizedLookupElement.withPriority(builder, MybatisConstants.PRIORITY));
                    continue;
                }

                //参数为实体的时候，不管怎么样，提示再说
                PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
                if (psiClass!=null){
                    PsiField[] fields = psiClass.getFields();
                    addLookupElements(result, fields);
                }

            }
        }
    }

    private static void addLookupElements(@NotNull CompletionResultSet result, PsiField[] fields) {
        for (PsiField field : fields) {
            LookupElementBuilder builder = LookupElementBuilder.create(field.getName())
                    .withIcon(PlatformIcons.FIELD_ICON);
            //变成一个有优先级的对象，其实不加也行
            result.addElement(builder);
        }
    }
}
