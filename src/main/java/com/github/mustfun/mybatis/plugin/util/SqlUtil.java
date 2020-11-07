package com.github.mustfun.mybatis.plugin.util;

import com.github.mustfun.mybatis.plugin.dom.model.*;
import com.intellij.psi.PsiElement;

/**
 * @author itar
 * @date 2020-03-02
 * 从sql中提炼表名
 */
public class SqlUtil {

    /**
     *
     * @param
     * @param position
     * @return
     */
    public static String getTableNameFromSql(IdDomElement element, PsiElement position){
        String value = element.getValue();
        String sql = value.replace("<", "").replace(">", "").replace("\n","").replace(position.getText(), "").toLowerCase();
        String tableName = null;

        try {
          if (element instanceof Select){
              tableName = sql.substring(sql.indexOf("from")+4, !sql.contains("where") ?sql.length():sql.indexOf("where")).trim();
          }else if (element instanceof Update){
              tableName = sql.substring(sql.indexOf("update")+6, !sql.contains("set") ? sql.indexOf("include") : sql.indexOf("set")).trim();
          }else if (element instanceof Insert){
              sql = sql.substring(0, !sql.contains("(") ?0:sql.indexOf(")")) + sql.substring(sql.indexOf(")")+1);
              tableName = sql.substring(sql.indexOf("into")+4, !sql.contains("values") ? sql.length() : sql.indexOf("values")).trim();
          }else if (element instanceof Delete){
              tableName = sql.substring(sql.indexOf("from")+4, !sql.contains("where") ? sql.length() : sql.indexOf("where")).trim();
          }
        } catch (Exception e) {
          return tableName;
        }
      return tableName;
    }
}
