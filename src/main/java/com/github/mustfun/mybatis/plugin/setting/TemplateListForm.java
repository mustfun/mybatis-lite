package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/19
 * @since 1.0
 */
@Setter
@Getter
public class TemplateListForm {
    private JPanel mainPanel;
    private JBScrollPane mainScrollPane;
    private JBList templateList;
    private JPanel firstPanel;
}
