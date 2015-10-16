package com.imxiaomai.wxplatform.dto;

/**
 * Created by zengyaowen on 15-10-15.
 */
public class OauthAccessTokenDTO {
    String openId;
    String token;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
