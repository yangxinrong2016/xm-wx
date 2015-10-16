package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.TemplateMsg;

public interface TemplateMsgDAO {

    int deleteByPrimaryKey(Integer id);

    int insert(TemplateMsg record);

    int insertSelective(TemplateMsg record);

    int updateByPrimaryKeySelective(TemplateMsg record);

    int updateByPrimaryKeyWithBLOBs(TemplateMsg record);

    int updateByPrimaryKey(TemplateMsg record);

    TemplateMsg selectByTemplateMsg(TemplateMsg record);

    TemplateMsg selectByPrimaryKey(Integer id);
}