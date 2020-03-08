package com.github.mustfun.mybatis.plugin.service.resolver;

import com.intellij.openapi.vfs.VirtualFile;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author itar
 * @date 2020-03-03
 * 抽象解析接口 - 专业解析Ymal 30年
 */
public class YamlFileResolver extends AbstractFileResolver<VirtualFile, Properties> {

    @Override
    String[] getPattern() {
        List<String> strings = Collections.singletonList("application-dev.yml");
        return strings.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Properties convert(VirtualFile map) {
        //典型普通yml文件
        Yaml yaml = new Yaml();
        try {
            Map<String, Object> source = yaml.load(new FileInputStream(new File(map.getPath())));
            Map<StringBuilder,Object> resultMap = new HashMap<>(20);
            tranHashMapToFlatMap(source, new StringBuilder(), resultMap);
            Properties properties = new Properties();
            properties.putAll(resultMap);
            return properties;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 平铺hashMap - 筛选关键字用
     * @param map
     * @param key  properties的键
     * @param resultMap 返回map
     */
    @SuppressWarnings("unchecked")
    private void tranHashMapToFlatMap(Map<String, Object> map,StringBuilder key,Map<StringBuilder,Object> resultMap) {
        for (String s : map.keySet()) {
            Object o = map.get(s);
            if(o instanceof Map){
                tranHashMapToFlatMap((Map) o,new StringBuilder(key).append(s).append("."),resultMap);
            }else{
                resultMap.put(new StringBuilder(key).append(s), o);
            }
        }
    }

}
