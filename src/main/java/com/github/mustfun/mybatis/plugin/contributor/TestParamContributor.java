package com.github.mustfun.mybatis.plugin.contributor;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author yanglin
 * @update itar
 */
public class TestParamContributor extends CompletionContributor {

    @SuppressWarnings("unchecked")
    public TestParamContributor() {
        extend(CompletionType.BASIC,
            XmlPatterns.psiElement()
                .inside(XmlPatterns.xmlAttributeValue().inside(XmlPatterns.xmlAttribute().withName("test"))),
            new CompletionProvider<CompletionParameters>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
                    @NotNull CompletionResultSet result) {
                    PsiElement position = parameters.getPosition();
                    addElementForPsiParameter(position.getProject(), result,
                            MapperUtils.findParentIdDomElement(position).orElse(null));
                }
            });
    }

    public static void addElementForPsiParameter(@NotNull Project project, @NotNull CompletionResultSet result,
        @Nullable IdDomElement element) {
        if (null == element) {
            return;
        }
        for (PsiParameter parameter : JavaUtils.findMethod(project, element).get().getParameterList().getParameters()) {
            Optional<String> valueText = JavaUtils.getAnnotationValueText(parameter, Annotation.PARAM);
            if (valueText.isPresent()) {
                LookupElementBuilder builder = LookupElementBuilder.create(valueText.get())
                    .withIcon(Icons.PARAM_COMPLETION_ICON);
                result.addElement(PrioritizedLookupElement.withPriority(builder, MybatisConstants.PRIORITY));
            }
        }
    }
}
