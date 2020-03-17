package com.github.mustfun.mybatis.plugin.setting.endpoint;

import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yanglin
 * @update itar
 * @function Mybatis配置项 - 项目级别的 - 持久化
 */
@State(
        name = "MybatisLiteProjectExtendSettings",
        storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)}
)
public class MybatisLiteProjectExtendSetting implements PersistentStateComponent<MybatisLiteProjectExtendSetting> {
    public Map<String, String> map;

    public MybatisLiteProjectExtendSetting() {
        this.map = new HashMap<>(2);
        map.put(MybatisConstants.CONFIG_FILE_NAME, "");
        map.put(MybatisConstants.SQL_PRINT_STATUS, "0");
    }

    public MybatisLiteProjectExtendSetting(Map<String,String> map) {
        this.map = map;
    }

    public static MybatisLiteProjectExtendSetting getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MybatisLiteProjectExtendSetting.class);
    }

    @Nullable
    @Override
    public MybatisLiteProjectExtendSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MybatisLiteProjectExtendSetting state) {
        //从state copy 到 this
        XmlSerializerUtil.copyBean(state, this);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}