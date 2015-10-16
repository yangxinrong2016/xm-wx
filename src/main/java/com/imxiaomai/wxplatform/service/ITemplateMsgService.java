package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.TemplateMsg;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface ITemplateMsgService {

    TemplateMsg getByWxIdAndOpenIdAndMsgId(String wxId, String openId, String msgId);

    void update(TemplateMsg templateMsg);

    int insert(TemplateMsg templateMsg);

    TemplateMsg getById(Integer id);
}
