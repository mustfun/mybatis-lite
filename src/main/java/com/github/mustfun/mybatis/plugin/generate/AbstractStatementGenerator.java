package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.service.EditorService;
import com.github.mustfun.mybatis.plugin.service.JavaService;
import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.ui.ListSelectionListener;
import com.github.mustfun.mybatis.plugin.ui.UiComponentFacade;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.google.common.base.Function;
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

import java.util.*;

/**
 * @author yanglin
 * @updater itar
 * @function 语句生成抽象类
 */
public abstract class AbstractStatementGenerator<T> {


    /**
     * 获取所有的生成器
     * @return
     */
    public static Set<AbstractStatementGenerator> getALLGenerator(){
        final AbstractStatementGenerator updateGenerator = new UpdateGenerator(MybatisConstants.DEFAULT_UPDATE_PATTEN);

        final AbstractStatementGenerator selectGenerator = new SelectGenerator(MybatisConstants.DEFAULT_SELECT_PATTEN);

        final AbstractStatementGenerator deleteGenerator = new DeleteGenerator(MybatisConstants.DEFAULT_DELETE_PATTEN);

        final AbstractStatementGenerator insertGenerator = new InsertGenerator(MybatisConstants.DEFAULT_INSERT_PATTEN);

        return ImmutableSet
                .of(updateGenerator, selectGenerator, deleteGenerator, insertGenerator);
    }

    /**
     * 获取xml文件的路径 ,简化为XXDao.xml
     */
    private static final Function<Mapper, String> FUN = new Function<Mapper, String>() {
        @Override
        public String apply(Mapper mapper) {
            VirtualFile vf = Objects.requireNonNull(mapper.getXmlTag()).getContainingFile().getVirtualFile();
            if (null == vf) {
                return "";
            }
            return vf.getName();
        }
    };

    public static Optional<PsiClass> getSelectResultType(@Nullable PsiMethod method) {
        if (null == method) {
            return Optional.empty();
        }
        PsiType returnType = method.getReturnType();
        if (returnType instanceof PsiPrimitiveType && !PsiType.VOID.equals(returnType)) {
            //如果是基本类型且不是void的时候
            return JavaUtils.findClazz(method.getProject(), Objects.requireNonNull(((PsiPrimitiveType) returnType).getBoxedTypeName()));
        } else if (returnType instanceof PsiClassReferenceType) {
            //如果是引用类型的时候
            PsiClassReferenceType type = (PsiClassReferenceType) returnType;
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                if (parameters.length == 1) {
                    type = (PsiClassReferenceType) parameters[0];
                }
            }
            return Optional.ofNullable(type.resolve());
        }
        return Optional.empty();
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
        GenerateModel model = new GenerateModel.StartWithModel();
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
        //找到相关联的xml文件放置于processor中
        JavaService.getInstance(method.getProject()).process(psiClass, processor);
        final List<Mapper> mappers = Lists.newArrayList(processor.getResults());
        if (1 == mappers.size()) {
            setupTag(method, Iterables.getOnlyElement(mappers, null));
        } else if (mappers.size() > 1) {
            Collection<String> paths = Collections2.transform(mappers, FUN);
            UiComponentFacade.getInstance(method.getProject())
                .showListPopup("【请选择目标mapper xml文件】", new ListSelectionListener() {
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
            T result = getTarget(mapper, method);
            setContent(mapper, result);

            GroupTwo target=(GroupTwo) result;
            XmlTag tag = target.getXmlTag();
            int offset = Objects.requireNonNull(tag).getTextOffset() + tag.getTextLength() - tag.getName().length() -3;
            EditorService editorService = EditorService.getInstance(method.getProject());
            editorService.format(tag.getContainingFile(), tag);
            editorService.scrollTo(tag, offset);
        });
    }

    /**
     * 给生成的语句加内容
     * @param mapper
     * @param target
     */
    protected abstract void setContent(@NotNull Mapper mapper, @NotNull T target);

    @Override
    public String toString() {
        return this.getDisplayText();
    }

    @NotNull
    protected abstract T getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method);

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
