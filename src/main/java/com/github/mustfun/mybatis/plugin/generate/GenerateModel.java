package com.github.mustfun.mybatis.plugin.generate;

import java.util.Collection;

/**
 * @author yanglin
 * @updater itar 生成模态对话框
 */
public abstract class GenerateModel {

    public static final GenerateModel START_WITH_MODEL = new StartWithModel();

    public static GenerateModel getInstance(String identifier) {
        try {
            return getInstance(Integer.valueOf(identifier));
        } catch (Exception e) {
            return START_WITH_MODEL;
        }
    }

    public static GenerateModel getInstance(int identifier) {
        switch (identifier) {
            case 0:
                return START_WITH_MODEL;
            default:
                throw new AssertionError();
        }
    }

    public boolean matchesAny(String[] patterns, String target) {
        for (String pattern : patterns) {
            if (apply(pattern, target)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesAny(Collection<String> patterns, String target) {
        return matchesAny(patterns.toArray(new String[patterns.size()]), target);
    }

    protected abstract boolean apply(String pattern, String target);

    public abstract int getIdentifier();

    /**
     * 以xxx为前缀的生成模型
     */
    static class StartWithModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.startsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 0;
        }
    }
}
