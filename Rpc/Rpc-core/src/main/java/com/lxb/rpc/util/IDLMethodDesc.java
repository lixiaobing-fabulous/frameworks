package com.lxb.rpc.util;


/**
 * 方法的接口描述语言类型描述
 */
public class IDLMethodDesc {

    /**
     * 应答包装对象固定字段
     */
    public static final String F_RESULT = "result";
    /**
     * 请求类型
     */
    protected IDLType request;
    /**
     * 应答类型
     */
    protected IDLType response;

    /**
     * 构造函数
     *
     * @param request  请求包装
     * @param response 应答包装
     */
    public IDLMethodDesc(IDLType request, IDLType response) {
        this.request = request;
        this.response = response;
    }

    public IDLType getRequest() {
        return request;
    }

    public IDLType getResponse() {
        return response;
    }

}
