package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/19
 * @since 1.0
 */
@Setter
@Getter
public class TemplateListForm {
    private Project project;
    private JPanel mainPanel;
    private JBScrollPane mainScrollPane;
    private JPanel firstPanel;
    private JBTable templateList;

    public TemplateListForm(Project project){
        this.project = project;
        templateList.setModel(new MyTableModel(new String[]{},new Object[][]{}));
        templateList.setDefaultRenderer(JButton.class, new ComboBoxCellRenderer());
        templateList.setRowSelectionAllowed(false);
        templateList.setColumnSelectionAllowed(false);
        templateList.setCellSelectionEnabled(false);
        templateList.setShowGrid(false);
        templateList.setShowHorizontalLines(true);
        templateList.setGridColor(templateList.getGridColor().brighter());
    }


    public static class MyTableModel extends AbstractTableModel {
        private String[] headName;
        private Object[][] obj;

        public MyTableModel() {
            super();
        }

        public MyTableModel(String[] headName, Object[][] obj) {
            this();
            this.headName = headName;
            this.obj = obj;
        }

        @Override
        public int getColumnCount() {
            return headName.length;
        }

        @Override
        public int getRowCount() {
            return obj.length;
        }

        @Override
        public Object getValueAt(int r, int c) {
            return obj[r][c];
        }

        @Override
        public String getColumnName(int c) {
            return headName[c];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return obj[0][columnIndex].getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 3 || columnIndex == 4) {
                return false;
            }
            return true;
        }
    }

    class ComboBoxCellRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JButton cmb = (JButton) value;
            cmb.setFont(table.getFont());
            return cmb;
        }
    }
}
