package com.github.mustfun.mybatis.plugin.intention;

import com.github.mustfun.mybatis.plugin.generate.AbstractStatementGenerator;
import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author yanglin
 * @update itar
 * @function 生成语句用
 */
public class GenerateStatementIntention extends GenericIntention {

    public GenerateStatementIntention() {
        super(GenerateStatementChooser.INSTANCE);
    }

    @NotNull
    @Override
    public String getText() {
        return "[Mybatis Lite] 生成xml-sql语句";
    }

    @Override
    public void invoke(@NotNull final Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
        if (!MybatisConstants.TRUE.equalsIgnoreCase(valueMap.get(MybatisConstants.NAVIGATION_OPEN_STATUS))) {
            return;
        }

        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        //generator开始生成语句
        AbstractStatementGenerator.applyGenerate(method);
    }

}
