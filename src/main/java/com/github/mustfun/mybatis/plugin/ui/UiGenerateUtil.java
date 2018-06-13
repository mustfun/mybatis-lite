package com.github.mustfun.mybatis.plugin.ui;

import com.intellij.ide.actions.AboutPopup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author yanglin
 */
public final class UiGenerateUtil {

    private Project project;

    private FileEditorManager fileEditorManager;

    private UiGenerateUtil(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static UiGenerateUtil getInstance(@NotNull Project project) {
        return new UiGenerateUtil(project);
    }

    public JBPopup getCommonPopUp(){
        JPopupMenu jPopupMenu = new JPopupMenu();
        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(jPopupMenu, null)
                .setRequestFocus(true)
                .setFocusable(true)
                .setResizable(false)
                .setMovable(false)
                .setModalContext(false)
                .setShowShadow(true)
                .setShowBorder(false)
                .setCancelKeyEnabled(true)
                .setCancelOnClickOutside(true)
                .setCancelOnOtherWindowOpen(true)
                .createPopup();
        return popup;
    }
}
