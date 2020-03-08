package com.github.mustfun.mybatis.plugin.service.resolver;

import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author itar
 * @date 2020-03-03
 * 解析抽象类
 */
public abstract class AbstractFileResolver<T, F> implements FileResolver<T, F> {

    /**
     * 获取解析的文件模式
     *
     * @return
     */
    String[] getPattern(){
        Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
        String s = valueMap.get(MybatisConstants.CONFIG_FILE_NAME);
        return new String[]{s};
    }

    public F beforeResolve(T t) {
        return null;
    }

    /**
     * @param t
     * @return
     */
    public F afterResolve(T t) {
        return null;
    }

    @Override
    public F resolve(T file) {
        String[] pattern = getPattern();
        beforeResolve(file);
        Map<String, VirtualFile> map = new LinkedHashMap<>();
        VfsUtilCore.visitChildrenRecursively((VirtualFile) file, new VirtualFileVisitor<VirtualFile>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                //不继续查看下面的子节点
                if (excludeFile(file)) {
                    return false;
                }
                if (file.getExtension()==null){
                    return true;
                }
                //application*.properties , db*.properties , application*.ymal
                for (String s : pattern) {
                    s= s.replace("*", "");
                    String[] split = s.split("\\.");
                    if(split.length<1){
                        continue;
                    }
                    if (file.getNameWithoutExtension().contains(split[0])&&file.getExtension().contains(split[1])){
                        map.put(file.getName(), file);
                    }
                }
                return true;
            }
        });
        if (map.isEmpty()){
            return null;
        }
        VirtualFile virtualFile = guessMostLikelyConfigFile(map);
        return convert(virtualFile);
    }

    /**
     * 获得测试环境的配置文件
     * @param map
     * @return
     */
    protected VirtualFile guessMostLikelyConfigFile(Map<String, VirtualFile> map) {
        VirtualFile configFile = null;
        for (String s : map.keySet()) {
            if (s.contains("dev")) {
                configFile = map.get(s);
            }
        }
        //首先找application-dev之类的文件，如果没有-dev之类的文件,就取第一个好了
        if (configFile == null) {
            configFile = map.get(map.keySet().iterator().next());
        }
        return configFile;
    }

    /**
     * 将解析到的路径转化为
     *
     * @param map
     * @return
     */
    protected abstract F convert(VirtualFile map);

    protected boolean excludeFile(VirtualFile file) {
        if (file.getPath().contains("/.git") || file.getPath().contains("/.idea")
                || file.getPath().contains("/.gradle") || file.getPath().contains("/.target")
                ||file.getPath().contains("/target/")) {
            return true;
        }
        return false;
    }

}
