package com.github.mustfun.mybatis.plugin.model.enums;

/**
 * @author dengzhiyuan
 * @version 1.0
 * @date 2018/5/17
 * @since 1.0
 */
public enum VmTypeEnums {
    RESULT(4,"Result层"),
    MODEL_PO(5,"Po层"),
    MODEL_BO(6,"Bo层"),
    MODEL_REQ(7,"请求Model层"),
    MODEL_RESP(8,"返回Model层"),
    DAO(9,"DAO层"),
    MAPPER(10,"Mapper文件"),
    SERVICE(11,"Service层"),
    SERVICE_IMPL(12,"ServiceImpl实现层"),
    CONTROLLER(13,"Controller层"),
    CONTROLLER_IMPL(14,"ControllerImpl实现层");

    private Integer code;
    private String  mgs;

    VmTypeEnums(Integer code, String mgs) {
        this.code = code;
        this.mgs = mgs;
    }

    public static VmTypeEnums findVmNameByVmType(Integer code){
        for (VmTypeEnums vmTypeEnums : VmTypeEnums.values()) {
            if (vmTypeEnums.getCode().equals(code)){
                return vmTypeEnums;
            }
        }
        return VmTypeEnums.RESULT;
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
