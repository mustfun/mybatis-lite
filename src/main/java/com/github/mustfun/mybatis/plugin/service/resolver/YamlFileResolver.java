package com.github.mustfun.mybatis.plugin.service.resolver;

import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.CollectionUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
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

    public YamlFileResolver(Project project) {
        super(project);
    }

    @Override
    String[] getPattern() {
        List<String> strings =null;
        String[] pattern = super.getPattern();
        List<String> result = new ArrayList<>();
        for (String s : pattern) {
            if (s.contains("yml")||s.contains("ymal")){
                result.add(s);
            }
        }
        if (CollectionUtils.isEmpty(result)){
            strings= Arrays.asList("application*.yml","application*.ymal");
        }else{
            strings = result;
        }
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
