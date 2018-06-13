package com.github.mustfun.mybatis.plugin.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/4/16
 * @since 1.0
 */
@Setter
@Getter
public class LocalColumn {
    private String columnName;
    private String dataType;
    private String columnComment;
    private Integer size;
    private Boolean nullable;
    private Integer position;

    private String extra;
    private String jpaColumnDefinition;
    private String attrName;
    private String attrLittleName;

    private String attrType;
    private String pk;
    private String columnKey;
}
