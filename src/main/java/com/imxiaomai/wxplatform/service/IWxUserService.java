package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.WxUser;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface IWxUserService {
    WxUser getByWxIdAndOpenid(String wxId, String openId);
    void insert(WxUser wxUser);
    void update(WxUser wxUser);
}
