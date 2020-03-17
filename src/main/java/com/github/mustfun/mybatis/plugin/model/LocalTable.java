package com.github.mustfun.mybatis.plugin.model;

import java.util.List;
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
public class LocalTable {

    private Integer id;
    private String tableName;
    private String tableType;
    private String comment;
    private String className;
    private String classLittleName;
    private List<LocalColumn> columnList;

    private LocalColumn pk;
}
