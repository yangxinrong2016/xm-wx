package com.imxiaomai.wxplatform.dto;

/**
 * Created by zengyaowen on 15-10-14.
 */
public class RetDTO {
    int code;
    String msg;
    Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
