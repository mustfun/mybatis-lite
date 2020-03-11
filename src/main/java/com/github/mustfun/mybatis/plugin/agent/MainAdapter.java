package com.github.mustfun.mybatis.plugin.agent;

import java.lang.instrument.Instrumentation;

/**
 * 格式
 *    "\033[*;*;*m"  //比如 "\033[1;2;3m"
 *
 *     前缀"\033["，后缀"m"
 *
 *     颜色、背景颜色、样式都是用数字表示
 *
 *     所以只需要把对应的字码用";"隔开就好了
 *
 * 范围
 *     转义符之后的字符都会变成转义符所表示的样式
 *
 * 样式
 *
 *     0  空样式
 *
 *     1  粗体
 *
 *     4  下划线
 *
 *     7  反色
 *
 *     颜色1：
 *
 *     30  白色
 *
 *     31  红色
 *
 *     32  绿色
 *
 *     33  黄色
 *
 *     34  蓝色
 *
 *     35  紫色
 *
 *     36  浅蓝
 *
 *     37  灰色
 *
 *     背景颜色：
 *
 *     40-47 和颜色顺序相同
 *
 *     颜色2：
 *
 *     90-97  比颜色1更鲜艳一些
 * @author itar
 * @date 2020-03-10
 * Main函数代理类
 */
public class MainAdapter {

    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("\033[33;4m" + "【Mybatis Lite】应用已开启增强模式" + "\033[0m");
        System.out.println("\033[33;4m" + "【Mybatis Lite】Sql增强已开启" + "\033[0m");
        // 添加Transformer
        inst.addTransformer(new SqlSessionTransformer());
    }
}
