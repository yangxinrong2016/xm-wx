package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.ResponseText;

public interface ResponseTextDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(ResponseText record);

    int insertSelective(ResponseText record);


    ResponseText selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResponseText record);

    int updateByPrimaryKeyWithBLOBs(ResponseText record);

    int updateByPrimaryKey(ResponseText record);
}