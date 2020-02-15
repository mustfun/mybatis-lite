package com.github.mustfun.mybatis.plugin.dom.model;

import com.intellij.util.xml.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 * @updater itar
 * @function mapper节点描述信息，为了找到mapper.xml起作用的
 * 就是根据这些描述信息找到mapper文件的
 */
public interface Mapper extends DomElement {

    /**
     * 共有节点 - 属性列表
     *
     * @return
     */
    @NotNull
    @SubTagsList({"insert", "update", "delete", "select"})
    public List<IdDomElement> getDaoElements();

    @Required
    /**
     * @NameValue表示返回string或者GenericValue两种属性
     */
    @NameValue
    @NotNull
    @Attribute("namespace")
    public GenericAttributeValue<String> getNamespace();

    @NotNull
    @SubTagList("resultMap")
    public List<ResultMap> getResultMaps();

    /**
     * mapper下面的子标签，parameterMap
     *
     * @return
     */
    @NotNull
    @SubTagList("parameterMap")
    public List<ParameterMap> getParameterMaps();

    /**
     * mapper下面的sql标签
     *
     * @return
     */
    @NotNull
    @SubTagList("sql")
    public List<Sql> getSqls();

    /**
     * insert标签
     *
     * @return
     */
    @NotNull
    @SubTagList("insert")
    public List<Insert> getInserts();

    /**
     * update标签
     *
     * @return
     */
    @NotNull
    @SubTagList("update")
    public List<Update> getUpdates();

    /**
     * delete标签
     *
     * @return
     */
    @NotNull
    @SubTagList("delete")
    public List<Delete> getDeletes();

    /**
     * select标签
     *
     * @return
     */
    @NotNull
    @SubTagList("select")
    public List<Select> getSelects();

    /**
     * 新增select标签,格式固定
     *
     * @return
     */
    @SubTagList("select")
    public Select addSelect();

    /**
     * 新增update标签
     *
     * @return
     */
    @SubTagList("update")
    public Update addUpdate();

    /**
     * 新增insert标签
     *
     * @return
     */
    @SubTagList("insert")
    public Insert addInsert();

    /**
     * 新增delete标签
     *
     * @return
     */
    @SubTagList("delete")
    public Delete addDelete();
}
