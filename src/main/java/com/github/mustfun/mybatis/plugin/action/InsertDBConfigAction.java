package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.model.ModuleConfig;
import com.github.mustfun.mybatis.plugin.setting.ModuleDBConfigUI;
import com.github.mustfun.mybatis.plugin.setting.SingleDBConnectInfoUI;
import com.github.mustfun.mybatis.plugin.ui.custom.ModuleDBConfigListPanel;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        List<SingleDBConnectInfoUI> uiList = new ArrayList<>();
        for (Module module : modules) {
            SingleDBConnectInfoUI dbconnectinfoui = new SingleDBConnectInfoUI();
            JPanel mainPanel = dbconnectinfoui.getMainPanel();
            String name = module.getName();
            //加监听
            dbconnectinfoui.getModuleName().setText(name+getBlank(maxLength-name.length()));
            Object config = ConnectionHolder.getInstance(project).getConfig(module.getName());
            if (config!=null){
                ModuleConfig moduleConfig = (ModuleConfig) config;
                dbconnectinfoui.getIpText().setText(moduleConfig.getDbAddress());
                dbconnectinfoui.getPortText().setText(moduleConfig.getPort()+"");
                dbconnectinfoui.getPasswordText().setText(moduleConfig.getPassword());
                dbconnectinfoui.getUserNameText().setText(moduleConfig.getUserName());
            }
            dbconnectinfoui.getIpText().getDocument().addDocumentListener(addInputListener(1,dbConfigUI,name,uiList));
            dbconnectinfoui.getPortText().getDocument().addDocumentListener(addInputListener(2,dbConfigUI,name,uiList));
            dbconnectinfoui.getUserNameText().getDocument().addDocumentListener(addInputListener(3,dbConfigUI,name,uiList));
            dbconnectinfoui.getPasswordText().getDocument().addDocumentListener(addInputListener(4,dbConfigUI,name,uiList));
            uiList.add(dbconnectinfoui);
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

    @NotNull
    private DocumentListener addInputListener(int i, ModuleDBConfigUI dbConfigUI, String module, List<SingleDBConnectInfoUI> modules) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setText(e, modules, module);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                setText(e, modules, module);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                setText(e, modules, module);
            }
            private void setText(DocumentEvent e, List<SingleDBConnectInfoUI> modules, String module) {
                if (!dbConfigUI.getMultiEdit().isSelected()){
                    return;
                }
                Document document = e.getDocument();
                for (SingleDBConnectInfoUI single : modules) {
                    if (!single.getModuleName().getText().trim().equals(module.trim())) {
                        switch (i){
                        case 1:
                            single.getIpText().setDocument(document);
                            break;
                        case 2:
                            single.getPortText().setDocument(document);
                            break;
                        case 3:
                            single.getUserNameText().setDocument(document);
                            break;
                        case 4:
                            single.getPasswordText().setDocument(document);
                            break;
                        default:
                            break;
                        }
                    }
                }
            }

        };
    }

    private String getBlank(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
