package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.ResponseTextDAO;
import com.imxiaomai.wxplatform.domain.ResponseText;
import com.imxiaomai.wxplatform.service.IResponseTextService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("responseTextServiceImpl")
public class ResponseTextServiceImpl implements IResponseTextService{
    @Resource
    ResponseTextDAO responseTextDAO;
    @Override
    public ResponseText selectByPrimaryKey(Integer id) {
        return responseTextDAO.selectByPrimaryKey(id);
    }
}
