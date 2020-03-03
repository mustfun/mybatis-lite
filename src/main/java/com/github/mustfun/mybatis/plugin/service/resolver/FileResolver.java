package com.github.mustfun.mybatis.plugin.service.resolver;

/**
 * @author itar
 * @date 2020-03-03
 * 文件解析接口 , T进F出
 */
public interface FileResolver<T,F> {
    /**
     *
     * @param file
     * @return
     */
    F resolve(T file);
}
