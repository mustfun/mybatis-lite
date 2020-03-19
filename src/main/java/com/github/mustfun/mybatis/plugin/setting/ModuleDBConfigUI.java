package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;


/**
 * @author itar
 * @date 2020-03-18
 * @function DB配置信息
 * JList只能单独显示一下，不能实现像panel那么智能.....
 *
 */
@Setter
@Getter
public class ModuleDBConfigUI {
    private JPanel mainPanel;
    private JBScrollPane scrollPane;
    private JCheckBox multiEdit;
}
