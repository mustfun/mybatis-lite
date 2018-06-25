package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.openapi.project.Project;
import com.intellij.ui.RightAlignedLabelUI;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
    private JBList templateList;
    private JPanel firstPanel;

    public TemplateListForm(Project project){
        this.project = project;
        JBList templateList = this.getTemplateList();
        templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateList.setCellRenderer(new FilesListCellRenderer());
        ArrayList<MyListComponent> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MyListComponent myListComponent = new MyListComponent();
            myListComponent.setjButton(new JButton("编辑"));
            myListComponent.setText("TEP"+i);
            dataList.add(myListComponent);
        }
        templateList.setModel(new TepListModel(dataList));
    }

    private class FilesListCellRenderer extends DefaultListCellRenderer {
        private FilesListCellRenderer() {
            setUI(new RightAlignedLabelUI());
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final Component rendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MyListComponent) {
                final MyListComponent path = (MyListComponent)value;
                String text="<html>"+path.getText()+"<br/> <html/>";
                setText(text);
            }
            return rendererComponent;
        }
    }


    /**
     * temp model
     */
    public class TepListModel extends  AbstractListModel {

        ArrayList<MyListComponent> uArray;

        public TepListModel(ArrayList<MyListComponent> uArray){
            this.uArray=uArray;
        }

        @Override
        public int getSize() {
            return uArray.size();
        }

        @Override
        public Object getElementAt(int index) {
            return   uArray.get(index) ;
        }

    }

    public class MyListComponent{
        private String text;
        private JButton jButton;

        public JButton getjButton() {
            return jButton;
        }

        public void setjButton(JButton jButton) {
            this.jButton = jButton;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
