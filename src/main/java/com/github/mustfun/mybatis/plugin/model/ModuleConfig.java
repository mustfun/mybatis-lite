package com.github.mustfun.mybatis.plugin.model;

import lombok.Getter;
import lombok.Setter;

/**
 * module的配置文件 - yml对应的配置信息
 * @author itar
 * @version 1.0
 * @date 2018/5/16
 * @since 1.0
 */
@Setter
@Getter
public class ModuleConfig extends DbSourcePo{
    /**
     * mybatis
     */
    private String typeAliasPackage;
}
