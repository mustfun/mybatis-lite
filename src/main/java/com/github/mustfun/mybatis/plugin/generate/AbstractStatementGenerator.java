package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.service.EditorService;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.setting.MybatisSetting;
import com.github.mustfun.mybatis.plugin.ui.ListSelectionListener;
import com.github.mustfun.mybatis.plugin.ui.UiComponentFacade;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author yanglin
 * @updater itar
 * @function 语句生成抽象类
 */
public abstract class AbstractStatementGenerator {


    /**
     * 获取所有的生成器
     * @return
     */
    public static Set<AbstractStatementGenerator> getALLGenerator(){
        final AbstractStatementGenerator updateGenerator = new UpdateGenerator("update", "modify", "set");

        final AbstractStatementGenerator selectGenerator = new SelectGenerator("select", "get", "look", "find",
                "list", "search", "count", "query");

        final AbstractStatementGenerator deleteGenerator = new DeleteGenerator("del", "cancel");

        final AbstractStatementGenerator insertGenerator = new InsertGenerator("insert", "add", "new","batch");

        return ImmutableSet
                .of(updateGenerator, selectGenerator, deleteGenerator, insertGenerator);
    }

    private static final Function<Mapper, String> FUN = new Function<Mapper, String>() {
        @Override
        public String apply(Mapper mapper) {
            VirtualFile vf = mapper.getXmlTag().getContainingFile().getVirtualFile();
            if (null == vf) {
                return "";
            }
            return vf.getCanonicalPath();
        }
    };

    public static Optional<PsiClass> getSelectResultType(@Nullable PsiMethod method) {
        if (null == method) {
            return Optional.absent();
        }
        PsiType returnType = method.getReturnType();
        if (returnType instanceof PsiPrimitiveType && returnType != PsiType.VOID) {
            return JavaUtils.findClazz(method.getProject(), ((PsiPrimitiveType) returnType).getBoxedTypeName());
        } else if (returnType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) returnType;
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                if (parameters.length == 1) {
                    type = (PsiClassReferenceType) parameters[0];
                }
            }
            return Optional.fromNullable(type.resolve());
        }
        return Optional.absent();
    }

    public static void applyGenerate(@Nullable final PsiMethod method) {
        if (null == method) {
            return;
        }
        final AbstractStatementGenerator[] generators = getGenerators(method);
        if (1 == generators.length) {
            //如果命中，直接执行即可
            generators[0].execute(method);
        } else {
            UiComponentFacade.getInstance(method.getProject())
                .showListPopup("[选择要生成的语句] ", new ListSelectionListener() {
                    @Override
                    public void selected(int index) {
                        generators[index].execute(method);
                    }
                    //自定义listener
                    @Override
                    public boolean isWriteAction() {
                        return true;
                    }

                }, generators);
        }
    }

    /**
     *
     * @param method
     * @return
     */
    @NotNull
    public static AbstractStatementGenerator[] getGenerators(@NotNull PsiMethod method) {
        GenerateModel model = MybatisSetting.getInstance().getStatementGenerateModel();
        String target = method.getName();
        List<AbstractStatementGenerator> result = Lists.newArrayList();
        Set<AbstractStatementGenerator> allGenerator = getALLGenerator();
        for (AbstractStatementGenerator generator : allGenerator) {
            //找出符合的generator
            if (model.matchesAny(generator.getPatterns(), target)) {
                result.add(generator);
            }
        }
        //指定了array的类型，避免是object类型,自动扩容吧
        return CollectionUtils.isNotEmpty(result) ? result.toArray(new AbstractStatementGenerator[0])
            : allGenerator.toArray(new AbstractStatementGenerator[result.size()]);
    }

    private Set<String> patterns;

    public AbstractStatementGenerator(@NotNull String... patterns) {
        this.patterns = Sets.newHashSet(patterns);
    }

    /**
     * 开始构建生成的语句
     * @param method
     */
    public void execute(@NotNull final PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (null == psiClass) {
            return;
        }
        CommonProcessors.CollectProcessor<Mapper> processor = new CommonProcessors.CollectProcessor<Mapper>();
        JavaService.getInstance(method.getProject()).process(psiClass, processor);
        final List<Mapper> mappers = Lists.newArrayList(processor.getResults());
        if (1 == mappers.size()) {
            setupTag(method, Iterables.getOnlyElement(mappers, null));
        } else if (mappers.size() > 1) {
            Collection<String> paths = Collections2.transform(mappers, FUN);
            UiComponentFacade.getInstance(method.getProject())
                .showListPopup("【请选择目标mapper xml文件路径】", new ListSelectionListener() {
                    @Override
                    public void selected(int index) {
                        setupTag(method, mappers.get(index));
                    }

                    @Override
                    public boolean isWriteAction() {
                        return true;
                    }
                }, paths.toArray(new String[0]));
        }
    }

    private void setupTag(PsiMethod method, Mapper mapper) {
        WriteCommandAction.runWriteCommandAction(method.getProject(), () -> {
            GroupTwo target = getTarget(mapper, method);
            target.getId().setStringValue(method.getName());
            target.setValue(" ");
            XmlTag tag = target.getXmlTag();
            int offset = tag.getTextOffset() + tag.getTextLength() - tag.getName().length() + 1;
            EditorService editorService = EditorService.getInstance(method.getProject());
            editorService.format(tag.getContainingFile(), tag);
            editorService.scrollTo(tag, offset);
        });
    }

    @Override
    public String toString() {
        return this.getDisplayText();
    }

    @NotNull
    protected abstract GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method);

    @NotNull
    public abstract String getId();

    @NotNull
    public abstract String getDisplayText();

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
