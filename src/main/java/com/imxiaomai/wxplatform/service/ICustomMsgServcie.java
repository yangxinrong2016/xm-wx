package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.CustomMsg;

/**
 * Created by zengyaowen on 15-10-15.
 */
public interface ICustomMsgServcie {

    public int insert(CustomMsg customMsg);

    public CustomMsg getById(Integer id);

    public void update(CustomMsg customMsg);
}
