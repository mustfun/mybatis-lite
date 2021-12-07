package com.github.mustfun.mybatis.plugin.contributor;

import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisDomUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.injected.editor.DocumentWindow;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;


/**
 * @author yanglin
 * @update itar
 * @function sql里面的代码补全
 */
public class SqlParamCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters,
        @NotNull final CompletionResultSet result) {
        //sql补全一般是普通类型的，不是smart
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }

        PsiElement position = parameters.getPosition();
        //这种方式拿到的就是xmlFile，如果用 position.getContainingFile(); 拿到的上层文件，不能拿到顶层的
        //sql是inject在xml里面的
        PsiFile topLevelFile = InjectedLanguageManager.getInstance(position.getProject()).getTopLevelFile(position);
        if (MybatisDomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                process(topLevelFile, result, position);
            }
        }
    }

    private void process(PsiFile xmlFile, CompletionResultSet result, PsiElement position) {
        // mybatis xml文件 如果配置成SQL类型，需要通过documentWindow.injectedToHost 获取真正的位置
        VirtualFile virtualFile = position.getContainingFile().getVirtualFile();
        int offset = position.getTextOffset();
        if (Objects.nonNull(virtualFile)) {
            DocumentWindow documentWindow = ((VirtualFileWindow) virtualFile).getDocumentWindow();
            offset = documentWindow.injectedToHost(position.getTextOffset());
        }
        Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(xmlFile.findElementAt(offset));
        if (idDomElement.isPresent()) {
            XmlParamContributor.addElementForPsiParameter(position.getProject(), result, idDomElement.get());
            result.stopHere();
        }
    }

    /**
     * @param file
     * @param offset sql如果有2行，那么就是上一行的offset加上本行的offset之和
     * @return
     * @function 倒序查找，如果看到{# 就开启代码补全
     */
    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        boolean closeFlag=false;
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c=='}'){
                closeFlag=true;
            }
            //如果有.应该应用.的唤醒，不应用下面唤醒
            if (c=='.'){
                return true;
            }
            if (c == '{' && text.charAt(i - 1) == '#'&&!closeFlag) {
                return true;
            }
        }
        return false;
    }
}