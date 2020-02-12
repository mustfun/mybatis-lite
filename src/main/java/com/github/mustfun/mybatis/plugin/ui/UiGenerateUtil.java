package com.github.mustfun.mybatis.plugin.ui;

import com.github.mustfun.mybatis.plugin.listener.CheckMouseListener;
import com.github.mustfun.mybatis.plugin.model.DbSourcePo;
import com.github.mustfun.mybatis.plugin.model.LocalTable;
import com.github.mustfun.mybatis.plugin.model.Template;
import com.github.mustfun.mybatis.plugin.service.DbService;
import com.github.mustfun.mybatis.plugin.service.SqlLiteService;
import com.github.mustfun.mybatis.plugin.setting.ConnectDbSetting;
import com.github.mustfun.mybatis.plugin.ui.custom.DialogWrapperPanel;
import com.github.mustfun.mybatis.plugin.util.ConnectionHolder;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.github.mustfun.mybatis.plugin.util.OrderedProperties;
import com.github.mustfun.mybatis.plugin.util.crypto.ConfigTools;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckBoxList;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

/**
 * @author itar
 * @version 1.0
 * @date 2018/6/12
 * @since jdk1.8
 */
public final class UiGenerateUtil {

    public static final String NUMBER_PATTEN = "^(\\w_)";
    private Project project;

    private FileEditorManager fileEditorManager;

    private ConnectDbSetting connectDbSetting;

    private UiGenerateUtil(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static UiGenerateUtil getInstance(@NotNull Project project) {
        return new UiGenerateUtil(project);
    }

    public DialogWrapperPanel getCommonDialog() {
        UiComponentFacade uiComponentFacade = UiComponentFacade.getInstance(project);
        if (null == connectDbSetting) {
            this.connectDbSetting = new ConnectDbSetting();
        }
        //监听主面板点击事件
        connectDbSetting.getConnectButton().addActionListener(getActionListener());

        //找出dao层所在目录
        JButton daoPanel = connectDbSetting.getDaoButton();
        VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
        VirtualFile daoPath = JavaUtils.getFilePattenPath(baseDir,  "Mapper.java", "Dao.java","/dao/", "/dal/");
        if (daoPath == null) {
            daoPath = baseDir;
        }
        connectDbSetting.getDaoInput().setText(daoPath.getPath());
        VirtualFile finalDaoPath = daoPath;
        daoPanel.addActionListener(e -> {
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择dao层存放目录", finalDaoPath, baseDir);
            if (vf == null) {
                return;
            }
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getDaoInput().setText(path);
        });

        //找出Mapper层所在目录
        JButton mapperButton = connectDbSetting.getMapperButton();
        VirtualFile mapperPath = JavaUtils.getFilePattenPath(baseDir,  "Mapper.xml", "Dao.xml","resources/mapper","resources/dao","resources/mybatis");
        if (mapperPath == null) {
            mapperPath = baseDir;
        }
        connectDbSetting.getMapperInput().setText(mapperPath.getPath());
        VirtualFile finalMapperPath = mapperPath;
        mapperButton.addActionListener(e -> {
            VirtualFile vf = uiComponentFacade
                .showSingleFolderSelectionDialog("请选择Mapper层存放目录", finalMapperPath, baseDir);
            if (vf == null) {
                return;
            }
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getMapperInput().setText(path);
        });

        //找出model所在的目录
        VirtualFile modelPath = JavaUtils.getFilePattenPath(baseDir, "/model/");
        if (modelPath == null) {
            modelPath = baseDir;
        }
        VirtualFile finalModelPath = modelPath;
        connectDbSetting.getPoInput().setText(modelPath.getPath());
        connectDbSetting.getPoButton().addActionListener(e -> {
            VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("请选择实体层存放目录", finalModelPath, baseDir);
            if (vf == null) {
                return;
            }
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getPoInput().setText(path);
        });

        // 找出Service所在的目录
        VirtualFile servicePath = JavaUtils.getFilePattenPath(baseDir, "/service/", "service.java");
        if (servicePath == null) {
            servicePath = baseDir;
        }
        VirtualFile finalServicePath = servicePath;
        connectDbSetting.getServiceInput().setText(servicePath.getPath());
        connectDbSetting.getServiceButton().addActionListener(e -> {
            VirtualFile vf = uiComponentFacade
                .showSingleFolderSelectionDialog("请选择Service层存放目录", finalServicePath, baseDir);
            if (vf == null) {
                return;
            }
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getServiceInput().setText(path);
        });

        // 找出controller所在的目录
        VirtualFile controllerPath = JavaUtils
            .getFilePattenPath(baseDir, "/facade/", "/controller/", "controller.java");
        if (controllerPath == null) {
            controllerPath = baseDir;
        }
        VirtualFile finalControllerPath = controllerPath;
        connectDbSetting.getControllerInput().setText(controllerPath.getPath());
        connectDbSetting.getControllerButton().addActionListener(e -> {
            VirtualFile vf = uiComponentFacade
                .showSingleFolderSelectionDialog("请选择Controller层存放目录", finalControllerPath, baseDir);
            //打印的就是选择的路径
            String path = vf.getPath();
            System.out.println("path = " + path);
            connectDbSetting.getControllerInput().setText(path);
        });

        //读取ymal或者property进行填充
        fillPanelText(connectDbSetting);

        return new DialogWrapperPanel(project, true, connectDbSetting);
    }

    @NotNull
    private ActionListener getActionListener() {
        return e -> {
            connectDbSetting.getTemplateCheckbox().clear();
            connectDbSetting.getTableCheckBox().clear();
            //监听点击
            String address = connectDbSetting.getAddress().getText();
            String port = connectDbSetting.getPort().getText();
            String dbName = connectDbSetting.getDbName().getText();
            if (StringUtils.isEmpty(dbName)) {
                Messages.showMessageDialog("DB名称配置不正确", "连接数据库提示", Messages.getInformationIcon());
                return;
            }
            String userName = connectDbSetting.getUserName().getText();
            String password = connectDbSetting.getPassword().getText();
            Integer p = null;
            if (StringUtils.isNotBlank(port)) {
                try {
                    p = Integer.parseInt(port);
                } catch (NumberFormatException e1) {
                    Messages.showMessageDialog("端口配置不正确", "连接数据库提示", Messages.getInformationIcon());
                    return;
                }
            }
            DbSourcePo dbSourcePo = new DbSourcePo();
            dbSourcePo.setPort(p);
            dbSourcePo.setDbAddress(address);
            dbSourcePo.setDbName(dbName);
            dbSourcePo.setUserName(userName);
            dbSourcePo.setPassword(password);
            //连接数据库
            DbService dbService = DbService.getInstance(project);
            Connection connection = dbService.getConnection(dbSourcePo);
            if (connection == null) {
                Messages.showMessageDialog("数据库连接失败", "连接数据库提示", Messages.getInformationIcon());
                return;
            }
            ConnectionHolder.addConnection(MybatisConstants.MYSQL_DB_CONNECTION, connection);
            List<LocalTable> tables = dbService.getTables(connection);
            CheckBoxList<String> tableCheckBox = connectDbSetting.getTableCheckBox();
            for (LocalTable table : tables) {
                tableCheckBox.addItem(table.getTableName(), table.getTableName(), false);
                Pattern compile = Pattern.compile(NUMBER_PATTEN);
                Matcher matcher = compile.matcher(table.getTableName());
                if (matcher.find()) {
                    String group = matcher.group(1);
                    connectDbSetting.getTablePrefixInput().setText(group);
                }

            }
            Connection sqlLiteConnection = dbService.getSqlLiteConnection();
            ConnectionHolder.addConnection(MybatisConstants.SQL_LITE_CONNECTION, sqlLiteConnection);
            SqlLiteService sqlLiteService = SqlLiteService.getInstance(sqlLiteConnection);
            //插入连接数据库的信息
            sqlLiteService.insertDbConnectionInfo(dbSourcePo);

            List<Template> templates = sqlLiteService.queryTemplateList();
            CheckBoxList<Integer> templateCheckbox = connectDbSetting.getTemplateCheckbox();
            for (Template template : templates) {
                templateCheckbox.addItem(template.getId(), template.getTepName(), false);
            }
            templateCheckbox.addMouseListener(new CheckMouseListener(project, 1, templates.get(2)));
        };
    }


    private void fillPanelText(ConnectDbSetting connectDbSetting) {
        //优先从历史记录里面读取
        if (readFromConnectLog(connectDbSetting)) {
            return;
        }
        VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
        VirtualFile file = JavaUtils
            .getFileByPattenName(baseDir, "application.properties", "application-dev.properties", "application.yml",
                "application-dev.yml");
        if (file == null) {
            return;
        }
        //读取yml文件
        File ymlFile = new File(file.getPath());
        if (file.getPath().contains(".yml") || file.getPath().contains(".yaml")) {
            insertPanelUseYaml(connectDbSetting, ymlFile);
        } else if (file.getPath().contains(".properties")) {
            insertPanelUseProperties(connectDbSetting, ymlFile);
        }
    }

    /**
     * 从数据库中读取最近一条历史记录
     */
    private boolean readFromConnectLog(ConnectDbSetting connectDbSetting) {
        Connection sqlLiteConnection = DbService.getInstance(project).getSqlLiteConnection();
        SqlLiteService sqlLiteService = SqlLiteService.getInstance(sqlLiteConnection);
        DbSourcePo dbSourcePo = sqlLiteService.queryLatestConnectLog();
        if (dbSourcePo != null) {
            connectDbSetting.getDbName().setText(dbSourcePo.getDbName());
            connectDbSetting.getAddress().setText(dbSourcePo.getDbAddress());
            connectDbSetting.getUserName().setText(dbSourcePo.getUserName());
            connectDbSetting.getPassword().setText(dbSourcePo.getPassword());
            return true;
        } else {
            return false;
        }
    }

    private void insertPanelUseProperties(ConnectDbSetting connectDbSetting, File file) {
        //读取properties类型文件
        try {
            OrderedProperties properties = new OrderedProperties();
            properties.load(new FileInputStream(file));
            boolean findUserName = true, findPassword = true, findUrl = true;
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                Object key = objectObjectEntry.getKey();
                boolean isMatchUserName = Pattern.matches(".*?druid(.*)(username$)", (String) key);
                if (isMatchUserName && findUserName) {
                    findUserName = false;
                    Object value = objectObjectEntry.getValue();
                    connectDbSetting.getUserName().setText((String) value);
                }
                boolean isMatchPassword = Pattern.matches(".*?druid(.*)(password$)", (String) key);
                if (isMatchPassword && findPassword) {
                    findPassword = false;
                    String password = (String) objectObjectEntry.getValue();
                    if (password != null && password.length() >= 64) {
                        password = ConfigTools.decrypt(password);
                    }
                    connectDbSetting.getPassword().setText(password);
                }
                boolean isMatchUrl = Pattern.matches(".*?druid(.*)(url$)", (String) key);
                if (isMatchUrl && findUrl) {
                    findUrl = false;
                    Object value = objectObjectEntry.getValue();
                    String[] s = ((String) value).split("/");
                    String[] split = s[2].split(":");
                    connectDbSetting.getAddress().setText(split[0]);
                    connectDbSetting.getPort().setText(split[1]);
                    String s1 = s[3].split("\\?")[0];
                    connectDbSetting.getDbName().setText(s1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertPanelUseYaml(ConnectDbSetting connectDbSetting, File ymlFile) {
        try {
            Yaml yaml = new Yaml();
            //读入文件
            Iterable<Object> result = yaml.loadAll(new FileInputStream(ymlFile));
            while (result.iterator().hasNext()) {
                Object next = result.iterator().next();
                String username = JavaUtils.findYamlValueByTag((LinkedHashMap) next, "username");
                connectDbSetting.getUserName().setText(username == null ? "root" : username);
                String password = JavaUtils.findYamlValueByTag((LinkedHashMap) next, "password");
                if (password != null && password.length() >= 64) {
                    password = ConfigTools.decrypt(password);
                }
                connectDbSetting.getPassword().setText(password);
                String url = JavaUtils.findYamlValueByTag((LinkedHashMap) next, "url");
                if (url != null && url.contains("jdbc:mysql")) {
                    String[] s = url.split("/");
                    String[] split = s[2].split(":");
                    connectDbSetting.getAddress().setText(split[0]);
                    connectDbSetting.getPort().setText(split[1]);
                    String s1 = s[3].split("\\?")[0];
                    connectDbSetting.getDbName().setText(s1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
