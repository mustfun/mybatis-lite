package com.github.mustfun.mybatis.plugin.agent;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author itar
 * @date 2020-03-10
 * 监听mybatis sql
 */
public class SqlSessionTransformer implements ClassFileTransformer {

    public static  Logger logger = LoggerFactory.getLogger(SqlSessionTransformer.class);

    public static byte[] getBytesFromFile(String fileName, byte[] rawClassBuffer) {
        try {
            //形如libs/xxxx.jar这样的,反正是共用同一个JVM , 或者用Project获取class路径也行...
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass(new ByteArrayInputStream(rawClassBuffer));
            setPrintSqlMethod(ctClass);
            CtMethod declaredMethod = ctClass.getDeclaredMethod("execute");
            String sql = "System.out.println(\"\033[31;1m\" + \"【Mybatis Lite】预处理SQL = \"+sql+ \"\033[0m\");";
            String param = "Object[] param = new Object[parametersSize];\n" +
                    "        for (int i=0;i<parametersSize;i++) {\n" +
                    "            param[i]=parameters[i].getValue();\n" +
                    "        }" +
                    "System.out.println(\"\033[31;1m\" + \"【Mybatis Lite】参数 = \"+java.util.Arrays.toString(param)+ \"\033[0m\");";
            String realSql = "String realSql = printRealSql(sql,parameters);" +
                    "if(realSql!=null){" +
                        "System.out.println(\"\033[31;1m\" + \"【Mybatis Lite】实际执行SQL = \"+realSql+ \"\033[0m\");" +
                    "}";
            declaredMethod.insertBefore(param);
            declaredMethod.insertBefore(sql);
            declaredMethod.insertAfter(realSql, false);
            return ctClass.toBytecode();
        } catch (Exception e) {
            System.out.println("增强"+fileName+"类出现异常!建议关闭Mybatis Lite插件自动打印SQl功能"
                    + e.getClass().getName()+e);
            return null;
        }
    }

    private static void setPrintSqlMethod(CtClass ctClass) throws CannotCompileException {
        String method = "public java.lang.String printRealSql(String sql, com.alibaba.druid.proxy.jdbc.JdbcParameter[] params) {\n" +
                "        if (sql==null||params==null||params.length==0){\n" +
                "            return null;\n" +
                "        }\n" +
                "        String newSql = new String(sql);\n" +
                "        Object[] values = new Object[params.length];\n" +
                "        for (int i = 0; i < params.length; i++) {\n" +
                "            if (params[i]==null){\n" +
                "                continue;\n" +
                "            }\n" +
                "            Object value = params[i].getValue();\n" +
                "            if (value instanceof java.sql.Date) {\n" +
                "                values[i] = \"'\" + value + \"'\";\n" +
                "            } else if (value instanceof java.lang.String) {\n" +
                "                values[i] = \"'\" + value + \"'\";\n" +
                "            } else if (value instanceof java.lang.Boolean) {\n" +
                "                if (((java.lang.Boolean)value)==java.lang.Boolean.TRUE){\n" +
                "                    values[i]=\"1\";\n" +
                "                }else{\n" +
                "                    values[i]=\"0\";\n" +
                "                }\n" +
                "            } else {\n" +
                "                values[i] = value;\n" +
                "            }\n" +
                "        }\n" +
                "        java.lang.String s = newSql.replaceAll(\"\\\\?\", \"%s\");\n" +
                "        return java.lang.String.format(s, values);\n" +
                "    }";
        CtMethod make = CtNewMethod.make(method, ctClass);
        ctClass.addMethod(make);
    }

    /**
     * 参数：
     * loader - 定义要转换的类加载器；如果是引导加载器，则为 null
     * className - 完全限定类内部形式的类名称和 The Java Virtual Machine Specification 中定义的接口名称。例如，"java/util/List"。
     * classBeingRedefined - 如果是被重定义或重转换触发，则为重定义或重转换的类；如果是类加载，则为 null
     * protectionDomain - 要定义或重定义的类的保护域
     * classfileBuffer - 类文件格式的输入字节缓冲区（不得修改）
     * 返回：
     * 一个格式良好的类文件缓冲区（转换的结果），如果未执行转换,则返回 null。
     * 抛出：
     * IllegalClassFormatException - 如果输入不表示一个格式良好的类文件
     */
    @Override
    public byte[] transform(ClassLoader l, String className, Class<?> c,
                            ProtectionDomain pd, byte[] rawClassBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        String transformName = "com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl";
        if (!className.equals(transformName)) {
            return null;
        }
        return getBytesFromFile(transformName, rawClassBuffer);
    }

    /**
     *  //增加本地变量
     *         method.addLocalVariable("startTime", CtClass.longType);
     *         method.addLocalVariable("endTime", CtClass.longType);
     *         //在方法前加入
     *         method.insertBefore("startTime = System.nanoTime(); System.out.println(\"enter " + method.getName() + " time \" + startTime);");
     *         //在方法后加入
     *         method.insertAfter("endTime = System.nanoTime(); System.out.println(\"leave " + method.getName() + " time \" + endTime);");
     *         method.insertAfter(" System.out.println(\"time difference " + method.getName() + " \" +(endTime - startTime));");
     *
     */
}
