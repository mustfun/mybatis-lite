package com.github.mustfun.mybatis.plugin.service.resolver;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author itar
 * @date 2020-03-03
 * 属性解析接口
 */
public class XmlConfigFileResolver extends AbstractFileResolver<VirtualFile,Properties>{

    public XmlConfigFileResolver(Project project) {
        super(project);
    }

    @Override
    String[] getPattern() {
        List<String> strings = Collections.singletonList("application-dev.xml");
        return strings.toArray(new String[0]);
    }


    @Override
    protected Properties convert(VirtualFile map) {
        return null;
    }


}
