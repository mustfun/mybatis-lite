package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.Delete;
import com.github.mustfun.mybatis.plugin.dom.model.GroupTwo;
import com.github.mustfun.mybatis.plugin.dom.model.Insert;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yanglin
 * @updater itar
 * @function 生成delete语句
 */
public class DeleteGenerator extends AbstractStatementGenerator<Delete> {

    public DeleteGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @Override
    protected void setContent(@NotNull Mapper mapper, @NotNull Delete target) {
        List<Delete> deletes = mapper.getDeletes();
        final String[] mostLikeTableName = new String[1];
        deletes.forEach(delete -> {
            String value = delete.getValue();
            if (StringUtils.isEmpty(value)){
                return ;
            }
            String[] froms = value.split("from");
            if (froms.length<2){
                return;
            }
            String from = froms[1];
            if (CollectionUtils.isNotEmpty(delete.getWheres())){
                //有where
                mostLikeTableName[0] = from;
                return;
            }
            mostLikeTableName[0] = from.split("where")[0];
        });
        target.setValue("\n        delete from "+ mostLikeTableName[0].trim()+"\n"+"    ");
    }


    @NotNull
    @Override
    protected Delete getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Delete delete = mapper.addDelete(mapper.getMergedDaoElements().size()+1);
        delete.getId().setStringValue(method.getName());
        return delete;
    }

    @NotNull
    @Override
    public String getId() {
        return "DeleteGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Delete 语句";
    }

}
