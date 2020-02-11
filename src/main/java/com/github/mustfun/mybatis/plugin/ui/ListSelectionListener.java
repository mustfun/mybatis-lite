package com.github.mustfun.mybatis.plugin.ui;

/**
 * @author yanglin
 * @updater itar
 * @function 列表选择监听者
 */
public interface ListSelectionListener extends ExecutableListener {

    /**
     * 列表某一项被选中
     * @param index
     */
    public void selected(int index);

}