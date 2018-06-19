package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
@Setter
@Getter
public class ConnectDbSetting {
    private JTextField address;
    private JTextField userName;
    private JTextField password;
    private JTextField port;
    private JButton connectButton;
    private JPanel mainPanel;
    private JLabel addressText;
    private JLabel portText;
    private JLabel userNameText;
    private JLabel passwordText;
    private JPanel listPanel;
    private JBScrollPane scrollPaneList;
    private CheckBoxList<String> tableCheckBox;
    private JTextField dbName;
    private JLabel dbNamePanel;
    private JBScrollPane templatePanel;
    private CheckBoxList templateCheckbox;
    private JBTextField packageName;
    private JBLabel daoLabel;

    public ConnectDbSetting(){
        port.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(!(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)){
                    e.consume(); //关键，屏蔽掉非法输入
                }
            }
        });
    }
}
