package com.imxiaomai.wxplatform.weixin.event;

import com.imxiaomai.wxplatform.util.Xml;

/**
 * Created by zengyaowen on 15-10-14.
 */
public interface IEventProcessor {
    public String process(String wxId, String openid, long createTime, Xml.ParsedXml parsedXml);
}
