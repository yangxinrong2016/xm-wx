package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.MenuDAO;
import com.imxiaomai.wxplatform.domain.Menu;
import com.imxiaomai.wxplatform.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zengyaowen on 15-10-13.
 */
@Service("menuServiceImpl")
public class MenuServiceImpl implements IMenuService{
    @Autowired
    private MenuDAO menuDAO;
    @Override
    public Menu getMenuByWxId(String weixinId) {
        return menuDAO.selectByWeixinId(weixinId);
    }
}
