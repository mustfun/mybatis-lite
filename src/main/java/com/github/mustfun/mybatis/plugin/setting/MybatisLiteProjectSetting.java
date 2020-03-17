package com.github.mustfun.mybatis.plugin.setting;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.github.mustfun.mybatis.plugin.util.MybatisConstants.CONFIG_FILE_NAME;


/**
 * @author yanglin
 * @update itar
 * @function Mybatis配置项 - 全局级别- 持久化
 */
@State(
        name = "MybatisLiteProjectSettings",
        storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class MybatisLiteProjectSetting implements PersistentStateComponent<MybatisLiteProjectSetting.MybatisLiteProjectState> {

    public static MybatisLiteProjectSetting getInstance(Project project) {
        return ServiceManager.getService(project,MybatisLiteProjectSetting.class);
    }

    public MybatisLiteProjectSetting() {
        this.mybatisLiteState = new MybatisLiteProjectState();
    }

    private MybatisLiteProjectState mybatisLiteState;

    /**
     * 每次保存设置都会调用这个方法，这个方法如果看到mybatisLiteState有变化，就会持久化
     * @return
     */
    @Nullable
    @Override
    public MybatisLiteProjectState getState() {
        return mybatisLiteState;
    }

    /**
     * loadState()方法在创建组件之后
     * （仅当组件有一些非默认的持久化状态时）和在外部更改具有持久化状态的XML文件之后
     * （例如，如果项目文件是从版本控制系统更新的）调用
     * @param state
     */
    @Override
    public void loadState(@NotNull MybatisLiteProjectState state) {
        this.mybatisLiteState = state;
    }

    public Map<String,String> getValueMap(){
        return mybatisLiteState.map;
    }

    /**
     * 存储字段的map
     * 构造函数决定最初初始值
     */
    static class MybatisLiteProjectState {
        public Map<String, String> map;

        public MybatisLiteProjectState() {
            this.map = new HashMap<>();
            map.put(CONFIG_FILE_NAME, "");
        }

        public MybatisLiteProjectState(Map<String, String> map) {
            this.map = map;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }
    }
}
