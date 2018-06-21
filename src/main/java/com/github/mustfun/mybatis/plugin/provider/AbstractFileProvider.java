package com.github.mustfun.mybatis.plugin.provider;


import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 文件提供者抽象类
 *
 * @author hehaiyangwork@gmail.com
 * @date 2017/04/07
 */
public abstract class AbstractFileProvider {

    protected Project project;

    protected String outputPath;

    public AbstractFileProvider(Project project, String outputPath) {
        this.project = project;
        this.outputPath = outputPath;
    }

    public abstract void create(String fullFile,String fileName);

    /**
     * 子目录？
     * @param psiDirectory
     * @param subPath
     * @param isResources
     * @return
     */
    protected PsiDirectory subDirectory(PsiDirectory psiDirectory, String subPath, Boolean isResources) {
        if (Objects.nonNull(isResources) && isResources) {
            psiDirectory = findResourcesDirectory(psiDirectory);
        }
        if (StringUtils.isNotEmpty(subPath)) {
            if ("/".equals(subPath.substring(0, 1))) {
                subPath = subPath.substring(1);
            }
            String[] subPathAttr = subPath.split("/");
            return createSubDirectory(psiDirectory, subPathAttr, 0);
        } else {
            return psiDirectory;
        }
    }

    /**
     * 创建子目录
     * @param psiDirectory
     * @param temp
     * @param level
     * @return
     */
    private PsiDirectory createSubDirectory(PsiDirectory psiDirectory, String[] temp, int level) {
        PsiDirectory subdirectory = psiDirectory.findSubdirectory(temp[level]);
        if (subdirectory == null) {
            subdirectory = psiDirectory.createSubdirectory(temp[level]);
        }
        if (temp.length != level + 1) {
            return createSubDirectory(subdirectory, temp, level + 1);
        }
        return subdirectory;
    }

    /**
     * 根据选择的package目录，找到resources目录
     * @param psiDirectory
     * @return
     */
    private PsiDirectory findResourcesDirectory(PsiDirectory psiDirectory) {

        PsiDirectory parentDirectory = psiDirectory.getParentDirectory();
        PsiDirectory iterator = psiDirectory.getParentDirectory();

        while (iterator != null && !iterator.getName().equals("main")) {
            iterator = iterator.getParentDirectory();
        }

        PsiDirectory resourcesDirectory = iterator == null ? null : iterator.findSubdirectory("resources");
        if (resourcesDirectory == null) {
            resourcesDirectory = parentDirectory.findSubdirectory("resources");
            if (resourcesDirectory == null) {
                resourcesDirectory = parentDirectory.createSubdirectory("resources");
            }
        }
        return resourcesDirectory;
    }

    protected PsiFile createFile(Project project, @NotNull PsiDirectory psiDirectory, String fileName, String context, LanguageFileType fileType) {
        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(fileName, fileType, context);
        // reformat class
        CodeStyleManager.getInstance(project).reformat(psiFile);
        if (psiFile instanceof PsiJavaFile) {
            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
            styleManager.optimizeImports(psiFile);
            styleManager.shortenClassReferences(psiFile);
        }
        psiDirectory.add(psiFile);
        return psiFile;
    }

}