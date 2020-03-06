package com.github.mustfun.mybatis.plugin.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 加载外部类用
 * @author itar
 */
public class ExternalClassLoader extends URLClassLoader {
    public ExternalClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ExternalClassLoader(URL[] urls) {
        super(urls);
    }
    public void addJar(URL url){
        this.addURL(url);
    }
}
