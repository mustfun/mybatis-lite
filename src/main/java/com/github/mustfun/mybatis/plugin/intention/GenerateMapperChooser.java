package com.github.mustfun.mybatis.plugin.intention;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * 自定义chooser , 主要是avaliable方法
 */
public class GenerateMapperChooser extends JavaFileIntentionChooser {

    static final JavaFileIntentionChooser INSTANCE = new GenerateMapperChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        if (isPositionOfInterfaceDeclaration(element)) {
            PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (null != clazz) {
                return !isTargetPresentInXml(clazz);
            }
        }
        return false;
    }

}
