package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.Menu;

public interface MenuDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKeyWithBLOBs(Menu record);

    int updateByPrimaryKey(Menu record);

    Menu selectByWeixinId(String weixinId);
}