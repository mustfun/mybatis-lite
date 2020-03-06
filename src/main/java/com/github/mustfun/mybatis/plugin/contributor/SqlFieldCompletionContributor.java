package com.github.mustfun.mybatis.plugin.contributor;

import com.github.mustfun.mybatis.plugin.dom.model.IdDomElement;
import com.github.mustfun.mybatis.plugin.init.InitMybatisLiteActivity;
import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.LocalColumn;
import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.service.DbServiceFactory;
import com.github.mustfun.mybatis.plugin.util.*;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.database.dataSource.DatabaseDriverManager;
import com.intellij.database.dialects.mysql.MysqlDialect;
import com.intellij.injected.editor.DocumentWindow;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Url;
import com.intellij.util.lang.UrlClassLoader;
import com.intellij.util.ui.classpath.SimpleClasspathElement;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

import static com.github.mustfun.mybatis.plugin.util.MybatisConstants.MODULE_DB_CONFIG;


/**
 * LocalDataSource localDataSource = LocalDataSource.create("test", "com.mysql.jdbc.Driver", dbSourcePo.getDbAddress(), dbSourcePo.getUserName());
 *         DatabaseCredentials credentials =DatabaseCredentials.getInstance();
 *         credentials.setPassword(localDataSource, new OneTimeString("root"));
 *         DatabaseSessionManager.Facade facade = DatabaseSessionManager.facade(project,localDataSource , credentials, null, null, DGDepartment.UNKNOWN);
 *         try {
 *             GuardedRef<DatabaseConnection> connect = facade.connect();
 *             DatabaseConnection databaseConnection = connect.get();
 *
 *         } catch (Exception e) {
 *             e.printStackTrace();
 *         }
 *
 * @date 2020-03-02
 * @author  itar
 * @function 写sql的时候补全字段
 */
public class SqlFieldCompletionContributor extends CompletionContributor {

    private static  final  Logger logger = LoggerFactory.getLogger(SqlFieldCompletionContributor.class);

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters,
        @NotNull final CompletionResultSet result) {
        //sql补全一般是普通类型的，不是smart
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }

        PsiElement position = parameters.getPosition();
        //这种方式拿到的就是xmlFile，如果用 position.getContainingFile(); 拿到的上层文件，不能拿到顶层的
        //sql是inject在xml里面的
        PsiFile topLevelFile = InjectedLanguageManager.getInstance(position.getProject()).getTopLevelFile(position);
        if (MybatisDomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                process(topLevelFile, result, position);
            }
        }
    }

    private void process(PsiFile xmlFile, CompletionResultSet result, PsiElement position) {
        //总而言之是为了拿到documentWindows
        VirtualFile virtualFile = position.getContainingFile().getVirtualFile();
        DocumentWindow documentWindow = ((VirtualFileWindow) virtualFile).getDocumentWindow();
        int offset = documentWindow.injectedToHost(position.getTextOffset());
        Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(xmlFile.findElementAt(offset));
        if (idDomElement.isPresent()) {
            addSqlFieldParameter(position.getProject(), result, idDomElement.get(),position);
            result.stopHere();
        }
    }

    /**
     * 展示字段的候选项吧
     * @param project
     * @param result
     * @param idDomElement
     * @param position
     */
    @SuppressWarnings("unchecked")
    private void addSqlFieldParameter(Project project, CompletionResultSet result, IdDomElement idDomElement, PsiElement position) {
        String tableName = SqlUtil.getTableNameFromSql(idDomElement,position);
        new InitMybatisLiteActivity().runActivity(project);
        Map<String, DbSourcePo> config = (Map<String, DbSourcePo>) ConnectionHolder.getInstance(project).getConfig(MODULE_DB_CONFIG);
        if (config==null){
            return;
        }
        //只有一个module的情况
        DbSourcePo dbSourcePo;
        if(config.size()==1){
            dbSourcePo = config.get(config.keySet().iterator().next());
        }else {
            //多个module根据名称来
            dbSourcePo = config.get(Objects.requireNonNull(Objects.requireNonNull(idDomElement.getModule()).getName()));
        }
        if (dbSourcePo==null){
            logger.warn("【Mybatis Lite】该模块下找不到合适的数据源");
            return ;
        }
        DbUtil dbUtil = new DbUtil(dbSourcePo.getDbAddress()+"&serverTimezone=GMT", dbSourcePo.getUserName(), dbSourcePo.getPassword());
        Connection connection = dbUtil.getConnection(project, idDomElement.getModule().getName());
        if (connection==null){
            logger.warn("【Mybatis Lite】===================获取不到链接");
            return;
        }
        DatabaseDriverManager instance = DatabaseDriverManager.getInstance();
        Collection<? extends DatabaseDriver> drivers = instance.getDrivers();
        for (DatabaseDriver driver : drivers) {
            logger.info(driver.getDriverClass());
            if (driver.getAdditionalClasspathElements().size()<1){
                continue;
            }
            if(!"MySQL".equals(driver.getName())){
                continue;
            }
            SimpleClasspathElement simpleClasspathElement = driver.getAdditionalClasspathElements().get(0);
            try {
                URL url= new URL(simpleClasspathElement.getClassesRootUrls().get(0).replaceAll("(?:/\\s*)+", "/"));
                ClassLoader classLoader = new ExternalClassLoader(new URL[]{url},ClassLoader.getSystemClassLoader());
                Class.forName(driver.getDriverClass(),false,classLoader);
                Properties props = new Properties();
                props.setProperty("user", dbSourcePo.getUserName());
                props.setProperty("password", dbSourcePo.getPassword());
                //设置可以获取remarks信息
                props.setProperty("remarks", "true");
                //设置可以获取tables remarks信息
                props.setProperty("useInformationSchema", "true");
                Connection connection1 = DriverManager.getConnection(dbSourcePo.getDbAddress()+"&serverTimezone=GMT", props);
                System.out.println("connection1 = " + connection1.getSchema());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<LocalTable> tables = DbServiceFactory.getInstance(project).createMysqlService().getTables(connection);
        for (LocalTable table : tables) {
            if (table.getTableName().equals(tableName)){
                addParameterToResult(table, result);
            }
        }

    }

    private void addParameterToResult(LocalTable table, CompletionResultSet result) {
        List<LocalColumn> columnList = table.getColumnList();
        for (LocalColumn localColumn : columnList) {
            LookupElementBuilder builder = LookupElementBuilder.create(localColumn.getColumnName())
                    .withIcon(Icons.FIELD_COMPLETION_ICON).withTypeText(table.getTableName());
            result.addElement(builder);
        }
    }

    /**
     * @param file
     * @param offset sql如果有2行，那么就是上一行的offset加上本行的offset之和
     * @return
     * @function 倒序查找，
     */
    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c=='a'){
                return true;
            }
        }
        return false;
    }
}