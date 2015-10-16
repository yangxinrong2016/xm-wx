package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.AccessToken;

public interface AccessTokenDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(AccessToken record);

    int insertSelective(AccessToken record);

    AccessToken selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccessToken record);

    int updateByPrimaryKey(AccessToken record);

    AccessToken selectByWeixinId(String weixinId);
}