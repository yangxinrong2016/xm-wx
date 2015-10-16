package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.JsapiTicketDAO;
import com.imxiaomai.wxplatform.domain.JsapiTicket;
import com.imxiaomai.wxplatform.service.IJsApiTicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-15.
 */
@Service("JsApiTicketServiceImpl")
public class JsApiTicketServiceImpl implements IJsApiTicketService {
    @Resource
    private JsapiTicketDAO jsapiTicketDAO;
    @Override
    public JsapiTicket getByWxId(String wxId) {
        return jsapiTicketDAO.selectByWxId(wxId);
    }
}
