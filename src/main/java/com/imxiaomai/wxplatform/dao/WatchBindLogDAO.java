package com.imxiaomai.wxplatform.dao;

import com.imxiaomai.wxplatform.domain.WatchBindLog;

public interface WatchBindLogDAO {
    int insert(WatchBindLog record);

    int insertSelective(WatchBindLog record);

}