package com.github.mustfun.mybatis.plugin.model.enums;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/5/17
 * @since 1.0
 */
public enum VmTypeEnums {
    DAO(1,"DAO层"),
    CONTROLLER(2,"Controller层"),
    CONTROLLER_IMPL(10,"ControllerImpl实现层"),
    SERVICE(3,"Service层"),
    SERVICE_IMPL(9,"ServiceImpl实现层"),
    MAPPER(4,"Mapper文件"),
    MODEL_PO(5,"Po层"),
    MODEL_BO(6,"Bo层"),
    MODEL_REQ(7,"请求Model层"),
    MODEL_RESP(8,"返回Model层"),
    RESULT(11,"Result层");

    private Integer code;
    private String  mgs;

    VmTypeEnums(Integer code, String mgs) {
        this.code = code;
        this.mgs = mgs;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMgs() {
        return mgs;
    }

    public void setMgs(String mgs) {
        this.mgs = mgs;
    }
}
