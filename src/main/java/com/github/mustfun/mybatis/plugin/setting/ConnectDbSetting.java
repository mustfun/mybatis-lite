package com.github.mustfun.mybatis.plugin.setting;

import com.github.mustfun.mybatis.plugin.model.Template;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author itar
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 * @ui design中改了不会立即生效，需要重启一下这个类，重新打开才能看到效果，暂时不知道解决办法
 *
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
    private CheckBoxList<Template> templateCheckbox;
    private JPanel configPanel;
    private JPanel generatePanel;
    private JBLabel tablePrefixLabel;
    private JBTextField tablePrefixInput;
    private JButton daoButton;
    private JBLabel daoLabel;
    private JBTextField daoInput;
    private JBLabel mapperLabel;
    private JBTextField mapperInput;
    private JButton mapperButton;
    private JBLabel poLabel;
    private JBTextField poInput;
    private JButton poButton;
    private JPanel daoPanel;
    private JPanel mapperPanel;
    private JPanel tablePrefixPanel;
    private JPanel poPanel;
    private JBLabel controllerLabel;
    private JBTextField controllerInput;
    private JPanel controllerPanel;
    private JButton controllerButton;
    private JPanel servicePanel;
    private JBTextField serviceInput;
    private JButton serviceButton;
    private JBLabel serviceLabel;
    private JCheckBox poStyle;
    private JBCheckBox daoPositionCheckBox;
    private JBCheckBox mapperPositionCheckBox;
    private JBCheckBox modelPositionCheckBox;
    private JBCheckBox controllerPositionCheckBox;
    private JBCheckBox servicePositionCheckBox;
    private JTextPane alertPane;

    /**
     * 可以快速运行，样式捉急，还是算了
     */
    /*public static void main(String[] args) {
        JFrame frame = new JFrame("ConnectDbSetting");
        frame.setContentPane(new ConnectDbSetting().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }*/

    public ConnectDbSetting() {
        port.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (!(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)) {
                    e.consume(); //关键，屏蔽掉非法输入
                }
            }
        });
        String text = "Tips:\r\n1.不勾选PO大写生成的Model文件为XXPo.java\r\n\r\n2.记住当前位置作用于当前项目\n" +
                "\n\r\n";
        alertPane.setText(text);
        alertPane.setToolTipText(text);
    }
}
