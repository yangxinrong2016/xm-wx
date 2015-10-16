package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.JsapiTicket;

public interface JsapiTicketDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(JsapiTicket record);

    int insertSelective(JsapiTicket record);

    JsapiTicket selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(JsapiTicket record);

    int updateByPrimaryKey(JsapiTicket record);

    JsapiTicket selectByWxId(String wxId);
}