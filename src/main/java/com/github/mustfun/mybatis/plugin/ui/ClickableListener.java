package com.github.mustfun.mybatis.plugin.ui;

/**
 * @author yanglin
 * @updater itar
 * @function 某一项被点击之后唤醒
 */
public interface ClickableListener extends ExecutableListener {

    /**
     * 被点击之后唤醒
     *
     */
    public void clicked();

}
