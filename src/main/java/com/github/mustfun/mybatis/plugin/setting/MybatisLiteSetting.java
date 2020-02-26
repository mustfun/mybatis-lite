package com.github.mustfun.mybatis.plugin.setting;

import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.github.mustfun.mybatis.plugin.util.MybatisConstants.NAVIGATION_OPEN_STATUS;


/**
 * @author yanglin
 * @update itar
 * @function Mybatis配置项 - 持久化
 */
@State(
        name = "MybatisLiteSettings",
        storages = @Storage(value = "$APP_CONFIG$/mybatis.lite.state.xml"))
public class MybatisLiteSetting implements PersistentStateComponent<MybatisLiteSetting.MybatisLiteState> {


    public static final String SEPARATOR = ",";

    public static MybatisLiteSetting getInstance() {
        return ServiceManager.getService(MybatisLiteSetting.class);
    }

    public MybatisLiteSetting() {
        this.mybatisLiteState = new MybatisLiteState();
    }

    private MybatisLiteState mybatisLiteState;

    /**
     * 每次保存设置都会调用这个方法，这个方法如果看到mybatisLiteState有变化，就会持久化
     * @return
     */
    @Nullable
    @Override
    public MybatisLiteState getState() {
        return mybatisLiteState;
    }

    /**
     * loadState()方法在创建组件之后
     * （仅当组件有一些非默认的持久化状态时）和在外部更改具有持久化状态的XML文件之后
     * （例如，如果项目文件是从版本控制系统更新的）调用
     * @param state
     */
    @Override
    public void loadState(@NotNull MybatisLiteState state) {
        this.mybatisLiteState = state;
    }

    /**
     * 存储字段的map
     */
    static class MybatisLiteState {
        public Map<String, String> map;

        public MybatisLiteState() {
            this.map = new HashMap<>();
            map.put(MybatisConstants.DEFAULT_INSERT_PATTEN_KEY, StringUtils.join(MybatisConstants.DEFAULT_INSERT_PATTEN,SEPARATOR));
            map.put(MybatisConstants.DEFAULT_UPDATE_PATTEN_KEY, StringUtils.join(MybatisConstants.DEFAULT_UPDATE_PATTEN,SEPARATOR));
            map.put(MybatisConstants.DEFAULT_DELETE_PATTEN_KEY, StringUtils.join(MybatisConstants.DEFAULT_DELETE_PATTEN,SEPARATOR));
            map.put(MybatisConstants.DEFAULT_SELECT_PATTEN_KEY, StringUtils.join(MybatisConstants.DEFAULT_SELECT_PATTEN,SEPARATOR));
            map.put(NAVIGATION_OPEN_STATUS, "1");
        }

        public MybatisLiteState(Map<String, String> map) {
            this.map = map;
        }
    }
}
