package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.JsapiTicket;

/**
 * Created by zengyaowen on 15-10-15.
 */
public interface IJsApiTicketService {
    public JsapiTicket getByWxId(String wxId);
}
