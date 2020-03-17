package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * @author itar
 * @version 1.0
 * @date 2018/6/15
 * @since 1.0
 * 查看模板时候用，语法高亮功能呢
 */
@Setter
@Getter
public class TemplateEdit {

    private JPanel mainPanel;
    private EditorTextField editorTextField;
}
