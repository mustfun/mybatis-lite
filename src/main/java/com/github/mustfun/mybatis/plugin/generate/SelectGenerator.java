package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.google.common.base.Optional;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yanglin
 * @updater itar
 * @function 生成select语句
 */
public class SelectGenerator extends AbstractStatementGenerator<Select> {

    //private static Pattern tableNamePatten = Pattern.compile("from.*?where");


    public SelectGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @Override
    protected void setContent(@NotNull Mapper mapper, @NotNull Select target) {
        List<Select> selects = mapper.getSelects();
         selects.forEach(select->{
            String value = select.getValue();
             String from = value.split("from")[1];
             String tableName = from.split("where")[0];
             tableName = tableName.replace("<", "");
             //System.out.println("tableName = " + tableName);
         });
        target.setValue("select * from");
    }

    @NotNull
    @Override
    protected Select getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Select select = mapper.addSelect();
        setupResultType(method, select);
        return select;
    }

    private void setupResultType(PsiMethod method, Select select) {
        Optional<PsiClass> clazz = AbstractStatementGenerator.getSelectResultType(method);
        if (clazz.isPresent()) {
            select.getResultType().setValue(clazz.get());
        }
    }

    @NotNull
    @Override
    public String getId() {
        return "SelectGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Select 语句";
    }
}
