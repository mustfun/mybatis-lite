package com.github.mustfun.mybatis.plugin.ui;

/**
 * @author yanglin
 * @updater itar
 * @function 一个监听接口
 */
public interface ExecutableListener {

    /**
     * 当前是否在写入操作
     * @return
     */
    public boolean isWriteAction();

}
