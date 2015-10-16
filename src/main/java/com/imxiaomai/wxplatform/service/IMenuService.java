package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.Menu;
/**
 * Created by zengyaowen on 15-10-13.
 */
public interface IMenuService {
    public Menu getMenuByWxId(String weixinId);
}
