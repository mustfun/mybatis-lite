package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class ConnectDbSetting extends JDialog{
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
    private JBScrollPane ScrollPaneList;
    private CheckBoxList<String> tableCheckBox;

    public ConnectDbSetting(){
        setContentPane(mainPanel);
        setModal(false);
        setLocationRelativeTo(null);
    }


    public JTextField getAddress() {
        return address;
    }

    public void setAddress(JTextField address) {
        this.address = address;
    }

    public JTextField getUserName() {
        return userName;
    }

    public void setUserName(JTextField userName) {
        this.userName = userName;
    }

    public JTextField getPassword() {
        return password;
    }

    public void setPassword(JTextField password) {
        this.password = password;
    }

    public JTextField getPort() {
        return port;
    }

    public void setPort(JTextField port) {
        this.port = port;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public void setConnectButton(JButton connectButton) {
        this.connectButton = connectButton;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public JLabel getAddressText() {
        return addressText;
    }

    public void setAddressText(JLabel addressText) {
        this.addressText = addressText;
    }

    public JLabel getPortText() {
        return portText;
    }

    public void setPortText(JLabel portText) {
        this.portText = portText;
    }

    public JLabel getUserNameText() {
        return userNameText;
    }

    public void setUserNameText(JLabel userNameText) {
        this.userNameText = userNameText;
    }

    public JLabel getPasswordText() {
        return passwordText;
    }

    public void setPasswordText(JLabel passwordText) {
        this.passwordText = passwordText;
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    public void setListPanel(JPanel listPanel) {
        this.listPanel = listPanel;
    }

    public JBScrollPane getScrollPaneList() {
        return ScrollPaneList;
    }

    public void setScrollPaneList(JBScrollPane scrollPaneList) {
        ScrollPaneList = scrollPaneList;
    }

    public CheckBoxList<String> getTableCheckBox() {
        return tableCheckBox;
    }

    public void setTableCheckBox(CheckBoxList<String> tableCheckBox) {
        this.tableCheckBox = tableCheckBox;
    }
}
