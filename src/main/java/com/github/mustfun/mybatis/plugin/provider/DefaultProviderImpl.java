package com.github.mustfun.mybatis.plugin.provider;


import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;

/**
 * 默认文件提供者
 *
 * @author hehaiyangwork@gmail.com
 * @date 2017/04/07
 * @update itar
 */
public class DefaultProviderImpl extends AbstractFileProvider {

    private static final Logger LOGGER = Logger.getInstance(DefaultProviderImpl.class);

    protected LanguageFileType languageFileType;

    public DefaultProviderImpl(Project project, String outputPath, LanguageFileType languageFileType) {
        super(project, outputPath);
        this.languageFileType = languageFileType;
    }

    @Override
    public PsiFile create(String fullFile, String fileName) {

        final PsiFile[] psiFile = {null};

        WriteCommandAction.runWriteCommandAction(this.project, () -> {
            try {
                VirtualFile vFile = VfsUtil.createDirectoryIfMissing(outputPath);
                PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(this.project).createDirectory(vFile);
                //PsiDirectory directory = subDirectory(psiDirectory, template.getSubPath(), template.getResources());

                //PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);

                psiFile[0] = createFile(project, psiDirectory, fileName, fullFile, this.languageFileType);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return psiFile[0];
    }

}