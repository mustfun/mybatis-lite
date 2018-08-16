package com.github.mustfun.mybatis.plugin.setting;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

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
        closeNaviRadioButton.setSelected(true);
    }

}
