package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.AccessToken;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface IAccessTokenService {
    AccessToken getAccessTokenByWxId(String weixinId);
    void insertAccessToken(AccessToken accessToken);
    void updateAccessToken(AccessToken accessToken);
}
