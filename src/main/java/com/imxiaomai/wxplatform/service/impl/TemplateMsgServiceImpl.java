package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.TemplateMsgDAO;
import com.imxiaomai.wxplatform.domain.TemplateMsg;
import com.imxiaomai.wxplatform.service.ITemplateMsgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("templateMsgServiceImpl")
public class TemplateMsgServiceImpl implements ITemplateMsgService {
    @Resource
    TemplateMsgDAO templateMsgDAO;
    @Override
    public TemplateMsg getByWxIdAndOpenIdAndMsgId(String wxId, String openId, String msgId) {
        TemplateMsg record = new TemplateMsg();
        record.setWxid(wxId);
        record.setOpenid(openId);
        record.setMsgid(msgId);
        return templateMsgDAO.selectByTemplateMsg(record);
    }

    @Override
    public void update(TemplateMsg templateMsg) {
        templateMsgDAO.updateByPrimaryKey(templateMsg);
    }

    @Override
    public int insert(TemplateMsg templateMsg) {
        return templateMsgDAO.insert(templateMsg);
    }

    @Override
    public TemplateMsg getById(Integer id) {
        return templateMsgDAO.selectByPrimaryKey(id);
    }
}
