package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.CustomMsgDAO;
import com.imxiaomai.wxplatform.domain.CustomMsg;
import com.imxiaomai.wxplatform.service.ICustomMsgServcie;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-15.
 */
@Service("customServiceImpl")
public class CustomServiceImpl implements ICustomMsgServcie {
    @Resource
    private CustomMsgDAO customMsgDAO;
    @Override
    public int insert(CustomMsg customMsg) {
        return customMsgDAO.insert(customMsg);
    }

    @Override
    public CustomMsg getById(Integer id) {
        return customMsgDAO.selectByPrimaryKey(id);
    }

    @Override
    public void update(CustomMsg customMsg) {
        customMsgDAO.updateByPrimaryKey(customMsg);
    }
}
