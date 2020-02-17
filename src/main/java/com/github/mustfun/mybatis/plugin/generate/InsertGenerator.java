package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.Insert;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 * @updater itar
 * @function 生成insert语句
 */
public class InsertGenerator extends AbstractStatementGenerator<Insert> {

    public InsertGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @Override
    protected void setContent(@NotNull Mapper mapper, @NotNull Insert target) {
        List<Insert> selects = mapper.getInserts();
        final String[] mostLikeTableName = new String[1];
        selects.forEach(select -> {
            String value = select.getValue();
            if (StringUtils.isEmpty(value)){
                return ;
            }
            String[] froms = value.split("into");
            if (froms.length<2){
                return;
            }
            String from = froms[1];
            String tableName = from.split("values")[0];
            if (StringUtils.isNotEmpty(tableName)){
                mostLikeTableName[0] = tableName;
            }
        });
        target.setValue("\n        insert into "+ mostLikeTableName[0].trim()+"\n"+"    ");
    }

    @NotNull
    @Override
    protected Insert getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addInsert();
    }

    @NotNull
    @Override
    public String getId() {
        return "InsertGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Insert 语句";
    }
}
