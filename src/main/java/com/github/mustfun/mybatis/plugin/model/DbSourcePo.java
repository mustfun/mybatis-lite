package com.github.mustfun.mybatis.plugin.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * @author itar
 * @version 1.0
 * @date 2018/5/16
 * @since 1.0
 */
@Setter
@Getter
public class DbSourcePo {

    private Integer id;
    private Integer port;
    private String userName;
    private String url;
    private String dbAddress;
    private String password;
    private String dbName;
    private Date createTime;
    private String moduleName;

    public DbSourcePo() {

    }

    public DbSourcePo(String dbAddress, Integer port, String dbName, String userName, String password, String moduleName) {
        this.port = port;
        this.userName = userName;
        this.dbAddress = dbAddress;
        this.password = password;
        this.dbName = dbName;
        this.moduleName = moduleName;
    }
}
