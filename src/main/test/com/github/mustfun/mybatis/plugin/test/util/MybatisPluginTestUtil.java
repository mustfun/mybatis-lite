package com.github.mustfun.mybatis.plugin.test.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.junit.Assert;

import java.io.File;

public class MybatisPluginTestUtil {
    public static final String BASE_TEST_DATA_PATH = findTestDataPath();
    public static final String SDK_HOME_PATH = BASE_TEST_DATA_PATH + "/sdk";

    private static String findTestDataPath() {
        if (new File(PathManager.getHomePath() + "/contrib").isDirectory()) {
            // started from IntelliJ IDEA Ultimate project
            return FileUtil.toSystemIndependentName(PathManager.getHomePath() + "/contrib/Dart/testData");
        }

        final File f = new File("testData");
        if (f.isDirectory()) {
            // started from 'mybatis-plugin' project
            return FileUtil.toSystemIndependentName(f.getAbsolutePath());
        }

        final String parentPath = PathUtil.getParentPath(PathManager.getHomePath());

        if (new File(parentPath + "/intellij-plugins").isDirectory()) {
            // started from IntelliJ IDEA Community Edition + mybatis Plugin project
            return FileUtil.toSystemIndependentName(parentPath + "/intellij-plugins/mybatis-lite/testData");
        }

        if (new File(parentPath + "/contrib").isDirectory()) {
            // started from IntelliJ IDEA Community + Dart Plugin project
            return FileUtil.toSystemIndependentName(parentPath + "/contrib/mybatis-lite/testData");
        }

        return "";
    }

    /**
     * 设置sdk
     */
    @TestOnly
    public void setUpProjectSdk(LightProjectDescriptor descriptor, CodeInsightTestFixture myFixture) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                Sdk sdk = descriptor.getSdk();
                ProjectJdkTable.getInstance().addJdk(sdk);
                ProjectRootManager.getInstance(myFixture.getProject()).setProjectSdk(sdk);
            }
        });
    }
}
