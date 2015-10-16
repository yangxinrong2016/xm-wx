package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.ResponseText;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface IResponseTextService {
    ResponseText selectByPrimaryKey(Integer id);
}
