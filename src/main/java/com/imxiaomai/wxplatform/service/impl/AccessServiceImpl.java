package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.AccessTokenDAO;
import com.imxiaomai.wxplatform.dao.MenuDAO;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.service.IAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("accessServiceImpl")
public class AccessServiceImpl implements IAccessTokenService {
    @Autowired
    private AccessTokenDAO accessTokenDAO;
    @Override
    public AccessToken getAccessTokenByWxId(String weixinId) {
        return accessTokenDAO.selectByWeixinId(weixinId);
    }

    @Override
    public void insertAccessToken(AccessToken accessToken) {
        accessTokenDAO.insert(accessToken);
    }

    @Override
    public void updateAccessToken(AccessToken accessToken) {
        accessTokenDAO.updateByPrimaryKey(accessToken);
    }
}
