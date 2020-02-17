package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.google.common.base.Optional;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.GenericDomValue;
import org.apache.commons.lang.StringUtils;
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
    public static final String SELECT_TAG = "<select>";

    //private static Pattern tableNamePatten = Pattern.compile("from.*?where");


    public SelectGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @Override
    protected void setContent(@NotNull Mapper mapper, @NotNull Select target) {
        List<Select> selects = mapper.getSelects();
        final String[] mostLikeTableName = new String[1];
        selects.forEach(select -> {
            String value = select.getValue();
            if (StringUtils.isEmpty(value)){
                return ;
            }
            String[] froms = value.split("from");
            if (froms.length<2){
                return;
            }
            String from = froms[1];
            String tableName = from.split("where")[0];
            tableName = tableName.replace("<", "");
            if (StringUtils.isNotEmpty(tableName)){
                mostLikeTableName[0] = tableName;
            }
        });
        target.setValue("\n        select * from "+ mostLikeTableName[0]+"\n"+"    ");
    }

    @NotNull
    @Override
    protected Select getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Select select = mapper.addSelect(mapper.getMergedDaoElements().size()+1);
        select.getId().setStringValue(method.getName());
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
