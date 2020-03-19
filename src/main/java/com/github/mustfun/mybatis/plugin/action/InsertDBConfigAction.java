package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.setting.ModuleDBConfigUI;
import com.github.mustfun.mybatis.plugin.setting.SingleDBConnectInfoUI;
import com.github.mustfun.mybatis.plugin.ui.custom.ModuleDBConfigListPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static com.intellij.uiDesigner.core.GridConstraints.*;

/**
 * @author itar
 * @version 1.0
 * @date 2020/03/12
 * @since 1.0
 * @function 插入DB配置用，为自动补全等提供服务，万不得已的时候使用，比如大量定制sql连接
 * 这是真的获取不到mysql连接，没办法
 */
public class InsertDBConfigAction extends AnAction {

    private static Component getListCellRendererComponent(JList<? extends JPanel> list, JPanel value, int index, boolean isSelected, boolean cellHasFocus) {
        return value;
    }

    /**
     * scrollPane（setView） + panel + 明细
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project==null){
            return ;
        }
        ModuleDBConfigUI dbConfigUI = new ModuleDBConfigUI();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        int maxLength = Objects.requireNonNull(Arrays.stream(modules).max(Comparator.comparingInt(x -> x.getName().length())).orElse(null)).getName().length();
        JBScrollPane scrollPane = dbConfigUI.getScrollPane();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(modules.length, 1, JBUI.emptyInsets(),5,1,true,false);
        JPanel viewPanel  = new JPanel(gridLayoutManager);
        JPanel topPanel = new JPanel();
        topPanel.add(viewPanel, new GridConstraints());
        topPanel.add(new JPanel(), new GridConstraints());
        int row=0;
        for (Module module : modules) {
            SingleDBConnectInfoUI dbconnectinfoui = new SingleDBConnectInfoUI();
            JPanel mainPanel = dbconnectinfoui.getMainPanel();
            String name = module.getName();
            dbconnectinfoui.getModuleName().setText(name+getBlank(maxLength-name.length()));
            //添加到list里面
            GridConstraints gridConstraints = new GridConstraints();
            gridConstraints.setAnchor(ANCHOR_NORTH);
            gridConstraints.setFill(FILL_NONE);
            gridConstraints.setRow(row++);
            viewPanel.add(mainPanel,gridConstraints);
        }
        //装配
        scrollPane.setViewportView(topPanel);
        ModuleDBConfigListPanel templateListPanel = new ModuleDBConfigListPanel(project, true, dbConfigUI);
        templateListPanel.show();

    }

    private String getBlank(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
