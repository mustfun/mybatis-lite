package com.github.mustfun.mybatis.plugin.setting;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yanglin
 * @update itar
 * @function form
 */
@Setter
@Getter
public class MybatisSettingForm {

    public JTextField insertPatternTextField;

    public JTextField deletePatternTextField;

    public JTextField updatePatternTextField;

    public JTextField selectPatternTextField;

    public JPanel mainPanel;

    public JComboBox modelComboBox;

    public JRadioButton openNaviButton;
    public JLabel openNaviLabel;
    public JRadioButton closeNaviRadioButton;

    public MybatisSettingForm() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(closeNaviRadioButton);
        buttonGroup.add(openNaviButton);
        openNaviButton.setSelected(true);
    }

}
