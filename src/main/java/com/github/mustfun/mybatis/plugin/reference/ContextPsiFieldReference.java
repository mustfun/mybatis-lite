package com.github.mustfun.mybatis.plugin.reference;

import com.github.mustfun.mybatis.plugin.dom.MapperBacktrackingUtils;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author yanglin
 * @updater itar
 * @function java属性跳转用
 */
public class ContextPsiFieldReference extends PsiReferenceBase<XmlAttributeValue> {

    protected ContextReferenceSetResolver resolver;

    protected int index;

    public ContextPsiFieldReference(XmlAttributeValue element, TextRange range, int index) {
        super(element, range, false);
        this.index = index;
        resolver = ReferenceSetResolverFactory.createPsiFieldResolver(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Optional resolved = resolver.resolve(index);
        return (PsiElement) resolved.get();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Optional<PsiClass> clazz = getTargetClazz();
        return clazz.isPresent() ? JavaUtils.findSettablePsiFields(clazz.get()) : PsiReference.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    private Optional<PsiClass> getTargetClazz() {
        if (getElement().getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            int ind = 0 == index ? 0 : index - 1;
            Optional resolved = resolver.resolve(ind);
            if (resolved.isPresent()) {
                return JavaService.getInstance(myElement.getProject()).getReferenceClazzOfPsiField((PsiElement) resolved.get());
            }
        } else {
            return MapperBacktrackingUtils.getPropertyClazz(myElement);
        }
        return Optional.empty();
    }

    public ContextReferenceSetResolver getResolver() {
        return resolver;
    }

    public void setResolver(ContextReferenceSetResolver resolver) {
        this.resolver = resolver;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
