package com.github.mustfun.mybatis.plugin.test.intention;

import com.github.mustfun.mybatis.plugin.intention.GenerateStatementIntention;
import com.github.mustfun.mybatis.plugin.test.util.MybatisPluginTestUtil;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;

public class GenerateXMLCodeTest extends BasePlatformTestCase {

    /**
     * UsefulTestCase.getTestName();
     * idea 2019.3路径
     *
     * @return
     */
    @Override
    protected String getTestDataPath() {
        String testDataPath = super.getTestDataPath();
        System.out.println("testDataPath = " + testDataPath);
        //其实就是相对路径的testData
        return MybatisPluginTestUtil.BASE_TEST_DATA_PATH;
    }

    @Test
    public void testGenerateStatement() throws Exception {
        //IdeaTestFixtureFactory fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
        //CodeInsightTestFixture codeInsightFixture = fixtureFactory.createCodeInsightFixture(myFixture);
        //codeInsightFixture.findSingleIntention("com.github.mustfun.mybatis.plugin.intention.GenerateStatementIntention");
        final String homePath = PathManager.getHomePath();

        System.out.println("homePath = " + homePath);
        myFixture.configureByFile("GenerateStatement-Input-File.md");
        Editor editor = myFixture.getEditor();

        GenerateStatementIntention generateStatementIntention = new GenerateStatementIntention();
        generateStatementIntention.invoke(getProject(), editor, myFixture.getFile());
        myFixture.checkResultByFile(getTestName(false) + "-Output-File.md");

    }

}
