package com.github.mustfun.mybatis.plugin.service.resolver;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author itar
 * @date 2020-03-03
 * 属性解析接口
 */
public class PropertiesFileResolver extends AbstractFileResolver<VirtualFile,Properties>{

    @Override
    String[] getPattern() {
        List<String> strings = Arrays.asList("application-dev.properties", "db.properties","dbConfig.properties");
        return strings.toArray(new String[0]);
    }



    @Override
    protected Properties convert(VirtualFile map) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(map.getPath())));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
