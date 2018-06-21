package com.github.mustfun.mybatis.plugin.provider;


import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.sql.SqlFileType;

/**
 * 文件提供者工厂
 * @author hehaiyangwork@gmail.com
 * @date 2017/3/17
 * @update itar
 */
public class FileProviderFactory {

    final private Project project;

    final private String outputPath;

    public FileProviderFactory(Project project, String outputPath) {
        this.project = project;
        this.outputPath = outputPath;
    }

    public AbstractFileProvider getInstance(String type) {
        if("java".equals(type)) {
            return new DefaultProviderImpl(project, outputPath, JavaFileType.INSTANCE);
        } else if("sql".equals(type)) {
            return new DefaultProviderImpl(project, outputPath, SqlFileType.INSTANCE);
        } else if("xml".equals(type)) {
            return new DefaultProviderImpl(project, outputPath, XmlFileType.INSTANCE);
        } else {
            return new DefaultProviderImpl(project, outputPath, JavaFileType.INSTANCE);
        }
    }
}