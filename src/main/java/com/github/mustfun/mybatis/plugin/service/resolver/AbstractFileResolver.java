package com.github.mustfun.mybatis.plugin.service.resolver;

import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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
    abstract String[] getPattern();

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
        Map<String, VirtualFile> map = new HashMap<>();
        VfsUtilCore.visitChildrenRecursively((VirtualFile) file, new VirtualFileVisitor<VirtualFile>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                //不继续查看下面的子节点
                if (excludeFile(file)) {
                    return false;
                }
                for (String s : pattern) {
                    if (file.getPath().contains(s)) {
                        map.put(s, file);
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

    protected VirtualFile guessMostLikelyConfigFile(Map<String, VirtualFile> map) {
        VirtualFile configFile = null;
        for (String s : map.keySet()) {
            if (map.get(s).getPath().contains("config")) {
                configFile = map.get(s);
            }
            if (configFile == null) {
                configFile = map.get(s);
            }
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
                || file.getPath().contains("/.gradle") || file.getPath().contains("/.target")) {
            return true;
        }
        return false;
    }

}
