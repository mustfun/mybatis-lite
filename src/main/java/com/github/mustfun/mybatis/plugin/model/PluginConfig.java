package com.github.mustfun.mybatis.plugin.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/21
 * @since 1.0
 */
@Setter
@Getter
public class PluginConfig {

    private Integer id;
    private String key;
    private String value;
}
