package com.github.mustfun.mybatis.plugin.ui;

import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.event.HyperlinkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public final class UiComponentFacade {

    private Project project;

    private FileEditorManager fileEditorManager;

    private UiComponentFacade(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static UiComponentFacade getInstance(@NotNull Project project) {
        return new UiComponentFacade(project);
    }

    /**
     * 展示 选一个文件夹
     */
    public VirtualFile showSingleFolderSelectionDialog(@NotNull String title,
        @Nullable VirtualFile toSelect,
        @Nullable VirtualFile... roots) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(title);
        if (null != roots) {
            descriptor.setRoots(roots);
        }
        descriptor.setHideIgnored(true);
        descriptor.setShowFileSystemRoots(true);

        return FileChooser.chooseFile(descriptor, project, toSelect);
    }

    /**
     * 展示一个可点击的文件夹
     */
    public JBPopup showListPopupWithSingleClickable(@NotNull String popupTitle,
        @NotNull ListSelectionListener popupListener,
        @NotNull String clickableTitle,
        @Nullable final ClickableListener clickableListener,
        @NotNull Object[] objs) {
        PopupChooserBuilder builder = createListPopupBuilder(popupTitle, popupListener, objs);
        JBCheckBox checkBox = new JBCheckBox(clickableTitle);
        builder.setSouthComponent(checkBox);
        final JBPopup popup = builder.createPopup();
        if (null != clickableListener) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    clickableListener.clicked();
                }
            };
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    popup.dispose();
                    setActionForExecutableListener(runnable, clickableListener);
                }
            });
        }
        setPositionForShown(popup);
        return popup;
    }

    /**
     * 展示一个list popup
     */
    public JBPopup showListPopup(@NotNull String title,
        @Nullable final ListSelectionListener listener,
        @NotNull Object[] objs) {
        PopupChooserBuilder builder = createListPopupBuilder(title, listener, objs);
        JBPopup popup = builder.createPopup();
        popup.setSize(new Dimension(200, 200));
        setPositionForShown(popup);
        return popup;
    }

    private void setPositionForShown(JBPopup popup) {
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (null != editor) {
            popup.showInBestPositionFor(editor);
        } else {
            popup.showCenteredInCurrentWindow(project);
        }
    }

    private void setActionForExecutableListener(Runnable runnable, ExecutableListener listener) {
        final Application application = ApplicationManager.getApplication();
        if (listener.isWriteAction()) {
            //唤醒系统写入操作，如果有读操作，写操作会被暂停
            application.runWriteAction(runnable);
        } else {
            //唤醒系统读取操作
            application.runReadAction(runnable);
        }
    }


    /**
     * 创建一系列可选列表
     */
    public PopupChooserBuilder createListPopupBuilder(@NotNull String title,
        @Nullable final ListSelectionListener listener,
        @NotNull Object... objs) {
        //toString方法展示
        final JBList list = new JBList(objs);
        list.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        PopupChooserBuilder builder = new PopupChooserBuilder(list);
        builder.setTitle(title);
        if (null != listener) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listener.selected(list.getSelectedIndex());
                }
            };
            builder.setItemChoosenCallback(new Runnable() {
                @Override
                public void run() {
                    //一单点击了就会唤醒这里
                    setActionForExecutableListener(runnable, listener);
                }
            });
        }
        return builder;
    }


    /**
     * 普通的popup组件
     */
    public JBPopup getCommonPopUp(JComponent component, String title, String text) {

        return JBPopupFactory.getInstance().createComponentPopupBuilder(component, null)
            /*
            .setResizable(false)
            .setShowShadow(true)
            .setCancelKeyEnabled(true)
            .setShowBorder(true)*/
            .setTitle(title)
            .setAdText(text)
            .setShowBorder(true)
            .setCancelButton(new IconButton("关闭", AllIcons.Actions.Close))
            .setRequestFocus(true)
            .setFocusable(true)
            .setMovable(false)
            .setCancelOnOtherWindowOpen(true)
            .setCancelOnClickOutside(false)
            .setProject(this.project)
            .createPopup();
    }


    /**
     * 将组件变成对话气泡
     *
     * <code>
     * Rectangle rect = connectDbSetting.getMainPanel().getVisibleRect(); Point p = new Point(rect.x + 30, rect.y +
     * rect.height - 10); RelativePoint point = new RelativePoint(connectDbSetting.getMainPanel(), p);
     * balloonBuilder.createBalloon().show(point, Balloon.Position.above);
     * </code>
     */
    @NotNull
    public BalloonBuilder buildBalloon(JComponent component) {
        JBInsets borderInsets = JBUI.insets(20, 20, 20, 20);
        return JBPopupFactory.getInstance()
            .createDialogBalloonBuilder(component, null)
            .setHideOnClickOutside(true)
            .setShadow(true)
            .setBlockClicksThroughBalloon(true)
            .setRequestFocus(true)
            .setBorderInsets(borderInsets);
    }


    /**
     * 弹出notify
     */

    public void buildNotify(Project project, String title, String content) {

        NotificationGroup notificationGroup = new NotificationGroup("Code Generate Success", STICKY_BALLOON, true);
        notificationGroup.createNotification(title, content, NotificationType.INFORMATION, new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                return;
            }
        }).notify(project);

    }
}
