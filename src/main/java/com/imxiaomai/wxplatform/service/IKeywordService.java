package com.imxiaomai.wxplatform.service;

import com.imxiaomai.wxplatform.domain.Keyword;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface IKeywordService {
    Keyword getByContent(String wxId, String content);
}
