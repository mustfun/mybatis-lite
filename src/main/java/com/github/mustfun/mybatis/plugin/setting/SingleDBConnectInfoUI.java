package com.github.mustfun.mybatis.plugin.setting;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * @author itar
 * @date 2020-03-18
 * @function 单个连接信息，便于布局
 */
@Setter
@Getter
public class SingleDBConnectInfoUI {
    private JLabel moduleName;
    private JComboBox dbTypeComboBox;
    private JTextField ipText;
    private JTextField portText;
    private JTextField userNameText;
    private JTextField passwordText;
    private JPanel mainPanel;
    private JTextField dbNameText;
}
