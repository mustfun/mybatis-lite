package com.github.mustfun.mybatis.plugin.model;

import lombok.Getter;
import lombok.Setter;

/**
 * module的配置文件
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/5/16
 * @since 1.0
 */
@Setter
@Getter
public class ModuleConfig extends DbSourcePo{
    private String typeAliasPackage;
}
