package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.WxUserDAO;
import com.imxiaomai.wxplatform.domain.WxUser;
import com.imxiaomai.wxplatform.service.IWxUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("wxUserServiceImpl")
public class WxUserServiceImpl implements IWxUserService {
    @Resource
    WxUserDAO wxUserDAO;
    @Override
    public WxUser getByWxIdAndOpenid(String wxId, String openId) {
        WxUser wxUser = new WxUser();
        wxUser.setWxid(wxId);
        wxUser.setOpenid(openId);
        return wxUserDAO.selectByWxUser(wxUser);
    }

    @Override
    public void insert(WxUser wxUser) {
        wxUserDAO.insert(wxUser);
    }

    @Override
    public void update(WxUser wxUser) {
        wxUserDAO.updateByPrimaryKey(wxUser);
    }
}
