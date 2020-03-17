package com.github.mustfun.mybatis.plugin.setting.endpoint;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.options.ConfigurableUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 设置扩展点 - area=application 如果等于project会造成 使用时候构造函数需要一个project参数
 *
 * @param <UI>
 * @param <S>
 * @author itar
 */
public abstract class AbstractEndpointTabConfigurable<UI extends ConfigurableUi<S>, S> extends ConfigurableBase<UI, S> {
    public static final ExtensionPointName<AbstractEndpointTabConfigurable> EP_NAME = ExtensionPointName.create("com.github.mustfun.plugin.mybatis.setting.projectConfigurable");

    protected AbstractEndpointTabConfigurable(@NotNull String id, @NotNull String displayName, @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
    }
}
