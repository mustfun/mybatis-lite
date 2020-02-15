package com.github.mustfun.mybatis.plugin.dom.model;

/**
 * @author yanglin
 * @updater itar
 */
public interface Update extends GroupTwo {

    /**
     * 格式固定或者用@TagValue注解
     */
    @Override
    String getValue();

    /**
     * 格式固定或者用@TagValue注解
     * @param s
     */
    @Override
    void setValue(String s);
}
