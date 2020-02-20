package com.github.mustfun.mybatis.plugin.reference;

import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @updater itar
 * @function 最原始的上下文解决类
 */
public abstract class ContextReferenceSetResolver<F extends PsiElement, K extends PsiElement> {

    private static final Splitter SPLITTER = Splitter.on(MybatisConstants.DOT_SEPARATOR);

    protected Project project;

    protected F element;

    protected List<String> texts;

    protected ContextReferenceSetResolver(@NotNull F element) {
        this.element = element;
        this.project = element.getProject();
        this.texts = Lists.newArrayList(SPLITTER.split(getText()));
    }

    @NotNull
    public final Optional<? extends PsiElement> resolve(int index) {
        Optional<K> startElement = getStartElement();
        return startElement.isPresent() ? (texts.size() > 1 ? parseNext(startElement, texts, index) : startElement)
            : Optional.empty();
    }

    private Optional<K> parseNext(Optional<K> current, List<String> texts, int index) {
        int ind = 1;
        while (current.isPresent() && ind <= index) {
            String text = texts.get(ind);
            if (text.contains(" ")) {
                return Optional.empty();
            }
            current = resolve(current.get(), text);
            ind++;
        }
        return current;
    }

    public Optional<K> getStartElement() {
        return getStartElement(Iterables.getFirst(texts, null));
    }

    @NotNull
    public abstract Optional<K> getStartElement(@Nullable String firstText);

    @NotNull
    public abstract String  getText();

    @NotNull
    public abstract Optional<K> resolve(@NotNull K current, @NotNull String text);

    public F getElement() {
        return element;
    }

    public void setElement(F element) {
        this.element = element;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
