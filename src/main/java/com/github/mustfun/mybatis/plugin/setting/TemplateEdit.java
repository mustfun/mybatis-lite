package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.components.JBScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/15
 * @since 1.0
 */
@Setter
@Getter
public class TemplateEdit {

    private JPanel mainPanel;
    private JTextArea codeArea;
    private JBScrollPane scrollCodeAreaPane;
}
