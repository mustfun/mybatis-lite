package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Insert;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.Update;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 * @updater itar
 * @function 生成update语句
 */
public class UpdateGenerator extends AbstractStatementGenerator<Update> {

    public static final String SET_FLAG = "<set>";

    public UpdateGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @Override
    protected void setContent(@NotNull Mapper mapper, @NotNull Update target) {
        List<Update> selects = mapper.getUpdates();
        final String[] mostLikeTableName = new String[1];
        selects.forEach(update -> {
            String value = update.getValue();
            if (StringUtils.isEmpty(value)){
                return ;
            }
            String[] froms = value.split("update");
            if (froms.length<2){
                return;
            }
            String from = froms[1];
            if (CollectionUtils.isNotEmpty(update.getIncludes())){
                //没set的时候，看下有没有where
                if (CollectionUtils.isNotEmpty(update.getWheres())){
                    //有where
                    mostLikeTableName[0] = from;
                    return;
                }
                mostLikeTableName[0] = from.split("where")[0];
                return ;
            }
            if (CollectionUtils.isNotEmpty(update.getSets())){
                if (CollectionUtils.isNotEmpty(update.getWheres())){
                    //有where
                    mostLikeTableName[0] = from;
                    return;
                }
                mostLikeTableName[0] = from.split("where")[0];
                return ;
            }
            mostLikeTableName[0] = from.split("set")[0];
        });
        target.setValue("\n        update "+ mostLikeTableName[0].trim()+" set"+"\n"+"    ");
    }


    @NotNull
    @Override
    protected Update getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Update update = mapper.addUpdate(mapper.getMergedDaoElements().size()+1);
        update.getId().setStringValue(method.getName());
        return update;
    }

    @NotNull
    @Override
    public String getId() {
        return "UpdateGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Update 语句";
    }

}
