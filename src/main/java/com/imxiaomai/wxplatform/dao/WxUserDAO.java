package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.WxUser;

public interface WxUserDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(WxUser record);

    int insertSelective(WxUser record);

    WxUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxUser record);

    int updateByPrimaryKey(WxUser record);

    WxUser selectByWxUser(WxUser record);
}