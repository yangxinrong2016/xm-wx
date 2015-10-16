package com.imxiaomai.wxplatform.service.impl;

import com.imxiaomai.wxplatform.dao.KeywordDAO;
import com.imxiaomai.wxplatform.dao.MenuDAO;
import com.imxiaomai.wxplatform.domain.Keyword;
import com.imxiaomai.wxplatform.domain.Menu;
import com.imxiaomai.wxplatform.service.IKeywordService;
import com.imxiaomai.wxplatform.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zengyaowen on 15-10-13.
 */
@Service("keywordServiceImpl")
public class KeywordServiceImpl implements IKeywordService{
    @Autowired
    private KeywordDAO keywordDAO;
    @Override
    public Keyword getByContent(String wxId, String content) {
        Keyword keyword = new Keyword();
        keyword.setWxid(wxId);
        keyword.setContent(content);
        return keywordDAO.selectByWxIdContent(keyword);
    }
}
