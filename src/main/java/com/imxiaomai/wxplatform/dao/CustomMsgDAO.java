package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.CustomMsg;

public interface CustomMsgDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(CustomMsg record);

    int insertSelective(CustomMsg record);

    CustomMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomMsg record);

    int updateByPrimaryKeyWithBLOBs(CustomMsg record);

    int updateByPrimaryKey(CustomMsg record);
}