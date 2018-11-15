package com.github.mustfun.mybatis.plugin.action;

import com.github.mustfun.mybatis.plugin.util.Icons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/6/12
 * @since 1.0
 */
public class GenerateCodeMainActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setVisible(true);
        //大家要求，编辑器关闭的时候也允许打开
        //event.getPresentation().setEnabled(editor != null);
        event.getPresentation().setIcon(Icons.MYBATIS_LOGO_MINI);
    }

    @Override
    public boolean hideIfNoVisibleChildren() {
        return false;
    }

}
