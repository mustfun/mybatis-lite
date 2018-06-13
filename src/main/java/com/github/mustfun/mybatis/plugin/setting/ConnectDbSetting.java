package com.github.mustfun.mybatis.plugin.setting;

import javax.swing.*;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/13
 * @since 1.0
 */
public class ConnectDbSetting {
    private JTextField address;
    private JTextField userName;
    private JTextField password;
    private JTextField port;
    private JButton connectButton;
    private JList<String> tableList;
    private JPanel mainPanel;
    private JLabel addressText;
    private JLabel portText;
    private JLabel userNameText;
    private JLabel passwordText;

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


    public JList<String> getTableList() {
        return tableList;
    }

    public void setTableList(JList<String> tableList) {
        this.tableList = tableList;
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
}
