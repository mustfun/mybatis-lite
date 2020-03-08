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
 * 心得： 只有当最后一个radioBox是want Grow的时候才会撑满后面的
 * 心得2： 两个panel互相嵌套时候
 */
@Setter
@Getter
public class MybatisSettingForm {

    public JPanel mainPanel;

    public JComboBox modelComboBox;

    public JRadioButton openNaviButton;
    public JLabel openNaviLabel;
    public JRadioButton closeNaviRadioButton;
    private JPanel generateSettingPanel;
    private JPanel naviPanel;
    private JTextField insertPatternInput;
    private JTextField updatePatternInput;
    private JTextField selectPattenInput;
    private JTextField deletePattenInput;
    private JPanel otherSettingPanel1;
    private JRadioButton sqlFieldOpenButton;
    private JRadioButton sqlFieldCloseButton;
    private JTextField bootConfigNameTextField;

    public MybatisSettingForm() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(closeNaviRadioButton);
        buttonGroup.add(openNaviButton);
        openNaviButton.setSelected(true);

        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(sqlFieldCloseButton);
        buttonGroup2.add(sqlFieldOpenButton);
    }

}
