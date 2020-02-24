package com.github.mustfun.mybatis.plugin.refactoring;

import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class MapperRefactoringProvider implements RefactoringElementListenerProvider {

    @Nullable
    @Override
    public RefactoringElementListener getListener(final PsiElement element) {
        if (!(element instanceof PsiClass)) {
            return null;
        }
        return new RefactoringElementListener() {
            @Override
            public void elementMoved(@NotNull PsiElement newElement) {
            }

            @Override
            public void elementRenamed(@NotNull final PsiElement newElement) {
                if (newElement instanceof PsiClass) {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            renameMapperXml((PsiClass) element, (PsiClass) newElement);
                        }
                    });
                }
            }
        };
    }

    private void renameMapperXml(@NotNull final PsiClass oldClazz, @NotNull final PsiClass newClazz) {
        Collection<Mapper> mappers = MapperUtils.findMappers(oldClazz.getProject(), oldClazz);
        try {
            for (Mapper mapper : mappers) {
                VirtualFile vf = Objects.requireNonNull(mapper.getXmlTag()).getOriginalElement().getContainingFile().getVirtualFile();
                if (null != vf) {
                    vf.rename(MapperRefactoringProvider.this, newClazz.getName() + "." + vf.getExtension());
                }
            }
        } catch (IOException e) {
        }
    }

}
