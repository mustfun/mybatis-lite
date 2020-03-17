package com.github.mustfun.mybatis.plugin.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * @author itar
 * @version 1.0
 * @date 2018/5/16
 * @since 1.0
 */

@Setter
@Getter
public class Template {

    private Integer id;
    private String tepName;
    private String tepDesc;
    private String tepContent;
    private String createBy;
    /**
     * 模板类型
     */
    private Integer vmType;
    /**
     * db类型
     */
    private Integer dbType;
    private Date createTime;
}
