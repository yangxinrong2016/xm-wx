package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.Keyword;

public interface KeywordDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(Keyword record);

    int insertSelective(Keyword record);

    Keyword selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Keyword record);

    int updateByPrimaryKey(Keyword record);

    Keyword selectByWxIdContent(Keyword record);
}