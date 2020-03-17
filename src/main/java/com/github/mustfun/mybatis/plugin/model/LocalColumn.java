package com.github.mustfun.mybatis.plugin.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author itar
 * @version 1.0
 * @date 2018/4/16
 * @since 1.0
 */
@Setter
@Getter
public class LocalColumn {

    /**
     * 列名
     */
    private String columnName;
    /**
     * 数据类型
     */
    private String dataType;
    private String columnComment;
    private Integer size;
    /**
     * 是否可空
     */
    private Boolean nullable;
    private Integer position;

    private String extra;
    private String jpaColumnDefinition;
    /**
     * 属性名称
     */
    private String attrName;
    /**
     * 属性小写名称
     */
    private String attrLittleName;

    /**
     * 属性类型
     */
    private String attrType;
    /**
     * 属性类型全路径
     */
    private String attrTypePath;
    /**
     * 主键
     */
    private String pk;
    private String columnKey;
}
