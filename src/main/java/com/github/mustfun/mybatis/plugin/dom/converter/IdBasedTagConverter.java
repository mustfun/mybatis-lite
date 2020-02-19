package com.github.mustfun.mybatis.plugin.dom.converter;

import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @version 1.0
 * @updater itar
 * @since jdk1.8
 * @function id解析器
 */
public abstract class IdBasedTagConverter extends AbstractConverterAdaptor<XmlAttributeValue> implements
    CustomReferenceConverter<XmlAttributeValue> {

    private final boolean crossMapperSupported;

    public IdBasedTagConverter() {
        this(true);
    }

    protected IdBasedTagConverter(boolean crossMapperSupported) {
        this.crossMapperSupported = crossMapperSupported;
    }

    /**
     * 从string转化为XmlAttributeValue
     * @param value
     * @param context
     * @return
     */
    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable @NonNls String value, ConvertContext context) {
        return matchIdDomElement(selectStrategy(context).getValue(), value, context).orNull();
    }

    /**
     * 从上文拿到的
     * @param idDomElements
     * @param value
     * @param context
     * @return
     */
    @NotNull
    private Optional<XmlAttributeValue> matchIdDomElement(Collection<? extends IdDomElement> idDomElements,
        String value, ConvertContext context) {
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        for (IdDomElement idDomElement : idDomElements) {
            if (MapperUtils.getIdSignature(idDomElement).equals(value) ||
                MapperUtils.getIdSignature(idDomElement, contextMapper).equals(value)) {
                return Optional.of(idDomElement.getId().getXmlAttributeValue());
            }
        }
        return Optional.absent();
    }

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue xmlAttribute, ConvertContext context) {
        DomElement domElement = DomUtil.getDomElement(xmlAttribute.getParent().getParent());
        if (!(domElement instanceof IdDomElement)) {
            return null;
        }
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        return MapperUtils.getIdSignature((IdDomElement) domElement, contextMapper);
    }

    /**
     * 选择一个id转换策略
     * @param context
     * @return
     */
    private AbstractTraverseStrategy selectStrategy(ConvertContext context) {
        //mapper交叉开启的时候，就用交叉策略，否则内部mapper策略，resultMap等只能在内部使用，这样更加节省性能
        return crossMapperSupported ? new CrossMapperStrategy(context) : new InsideMapperStrategy(context);
    }

    /**
     * @param mapper mapper in the project, null if {@link #crossMapperSupported} is false
     * @param context the dom convert context
     */
    @NotNull
    public abstract Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context);

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element,
        ConvertContext context) {
        return PsiClassConverter.createJavaClassReferenceProvider(value, null, new ValueReferenceProvider(context))
            .getReferencesByElement(element);
    }


    /**
     * 抽象遍历策略
     */
    private abstract static class AbstractTraverseStrategy {

        protected ConvertContext context;

        public AbstractTraverseStrategy(@NotNull ConvertContext context) {
            this.context = context;
        }

        public abstract Collection<? extends IdDomElement> getValue();
    }

    private class InsideMapperStrategy extends AbstractTraverseStrategy {

        public InsideMapperStrategy(@NotNull ConvertContext context) {
            super(context);
        }

        @Override
        public Collection<? extends IdDomElement> getValue() {
            return getComparisons(null, context);
        }

    }

    /**
     * 跳转mapper策略 - getValue方法提供所有的mapper文件
     */
    private class CrossMapperStrategy extends AbstractTraverseStrategy {

        public CrossMapperStrategy(@NotNull ConvertContext context) {
            super(context);
        }

        @Override
        public Collection<? extends IdDomElement> getValue() {
            List<IdDomElement> result = Lists.newArrayList();
            //找到所有的xml文件
            Collection<Mapper> mappers = MapperUtils.findMappers(context.getProject());
            for (Mapper mapper : mappers) {
                result.addAll(getComparisons(mapper, context));
            }
            return result;
        }

    }


    private class ValueReferenceProvider extends JavaClassReferenceProvider {

        private ConvertContext context;

        private ValueReferenceProvider(ConvertContext context) {
            this.context = context;
        }

        @Nullable
        @Override
        public GlobalSearchScope getScope(Project project) {
            return GlobalSearchScope.allScope(project);
        }

        /**
         * It looks like hacking here, as it's a little hard to handle so many different cases as JetBrains does
         */
        @NotNull
        @Override
        public PsiReference[] getReferencesByString(String text, @NotNull PsiElement position, int offsetInPosition) {
            List<PsiReference> refs = Lists.newArrayList(super.getReferencesByString(text, position, offsetInPosition));
            ValueReference vr = new ValueReference(position, getTextRange(position), context, text);
            if (!refs.isEmpty() && 0 != vr.getVariants().length) {
                refs.remove(refs.size() - 1);
                refs.add(vr);
            }
            return refs.toArray(new PsiReference[refs.size()]);
        }

        private TextRange getTextRange(PsiElement element) {
            String text = element.getText();
            int index = text.lastIndexOf(MybatisConstants.DOT_SEPARATOR);
            return -1 == index ? ElementManipulators.getValueTextRange(element)
                : TextRange.create(text.substring(0, index).length() + 1, text.length() - 1);
        }
    }

    /**
     * 引用代码
     */
    private class ValueReference extends PsiReferenceBase<PsiElement> {

        private ConvertContext context;
        private String text;

        public ValueReference(@NotNull PsiElement element, TextRange rng, ConvertContext context, String text) {
            super(element, rng, false);
            this.context = context;
            this.text = text;
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            return IdBasedTagConverter.this.fromString(text, context);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            Set<String> res =
                getElement().getText().contains(MybatisConstants.DOT_SEPARATOR) ? setupContextIdSignature()
                    : setupGlobalIdSignature();
            return res.toArray(new String[res.size()]);
        }

        private Set<String> setupContextIdSignature() {
            Set<String> res = Sets.newHashSet();
            String ns = text.substring(0, text.lastIndexOf(MybatisConstants.DOT_SEPARATOR));
            for (IdDomElement ele : selectStrategy(context).getValue()) {
                if (MapperUtils.getNamespace(ele).equals(ns)) {
                    res.add(MapperUtils.getId(ele));
                }
            }
            return res;
        }

        private Set<String> setupGlobalIdSignature() {
            Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
            Collection<? extends IdDomElement> idDomElements = selectStrategy(context).getValue();
            Set<String> res = new HashSet<String>(idDomElements.size());
            for (IdDomElement ele : idDomElements) {
                res.add(MapperUtils.getIdSignature(ele, contextMapper));
            }
            return res;
        }

    }

}
