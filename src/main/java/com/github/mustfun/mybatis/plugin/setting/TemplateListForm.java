package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.ide.presentation.VirtualFilePresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.readOnlyHandler.FileListRenderer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.RightAlignedLabelUI;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.PlatformIcons;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
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
    private JBList templateList;
    private JPanel firstPanel;
    private JPanel buttonPanel;
    private JLabel alertLabel;

    public TemplateListForm(Project project){
        this.project = project;
        JBList templateList = this.getTemplateList();
        templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateList.setCellRenderer(new FilesListCellRenderer());
    }

    private class FilesListCellRenderer extends DefaultListCellRenderer {
        private FilesListCellRenderer() {
            setUI(new RightAlignedLabelUI());
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final Component rendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof String) {
                final String path = (String)value;
                final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
                if (file == null) {
                    setForeground(JBColor.RED);
                    setIcon(null);
                }
                else {
                    //setForeground(myFilesList.getForeground());
                    setIcon(file.isDirectory() ? PlatformIcons.FOLDER_ICON : VirtualFilePresentation.getIcon(file));
                }
                setText(path);
            }
            if (value instanceof JComponent){

            }
            return rendererComponent;
        }
    }

}
