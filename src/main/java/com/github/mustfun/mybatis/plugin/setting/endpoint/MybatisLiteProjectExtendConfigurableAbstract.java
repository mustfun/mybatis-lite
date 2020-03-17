package com.github.mustfun.mybatis.plugin.setting.endpoint;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


/**
 * @author itar
 * @function 项目级配置
 *
 */
public class MybatisLiteProjectExtendConfigurableAbstract extends AbstractEndpointTabConfigurable<MybatisLiteProjectExtendConfigurableUi, MybatisLiteProjectExtendSetting> {

    private Project project;


    MybatisLiteProjectExtendConfigurableAbstract(Project project) {
        super("MybatisLiteProjectSetting", "Mybatis Lite", null);
        this.project = project;
    }


    @NotNull
    @Override
    protected MybatisLiteProjectExtendSetting getSettings() {
        return MybatisLiteProjectExtendSetting.getInstance(project);
    }

    @Override
    protected MybatisLiteProjectExtendConfigurableUi createUi() {
        return new MybatisLiteProjectExtendConfigurableUi(project);
    }
}
