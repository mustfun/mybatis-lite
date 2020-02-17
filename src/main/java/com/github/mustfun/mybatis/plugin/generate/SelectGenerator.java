package com.github.mustfun.mybatis.plugin.generate;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.dom.model.Select;
import com.google.common.base.Optional;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
        setupResultType(method, select, mapper);
        return select;
    }

    private void setupResultType(PsiMethod method, Select select, Mapper mapper) {
        Optional<PsiClass> clazz = AbstractStatementGenerator.getSelectResultType(method);
        if (clazz.isPresent()) {
            final String[] xmlTag = {null};
            mapper.getResultMaps().forEach(map->{
                if (Objects.requireNonNull(map.getType().getRawText()).equalsIgnoreCase(clazz.get().getQualifiedName())){
                    xmlTag[0] = map.getId().getRawText();
                }
            });
            if (!xmlTag[0].isEmpty()){
                //我比较喜欢用resultMap一些
                select.getResultMap().setStringValue(xmlTag[0]);
                return ;
            }
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
