package com.imxiaomai.wxplatform.weixin.service;

import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by zengyaowen on 16/3/9.
 */
@Service("financeService")
public class FinanceService {
    private static final Logger log = Logger.getLogger(FinanceService.class);
    @Value("${financeActive.url}")
    private String FINANCE_ACTIVE_URL;
    /**
     * 推送用户关注事件给金融
     *
     * @param openid
     * @param event
     * @param msg
     */
    public void pushUserActivity(String openid, int sourceId){
        if(log.isDebugEnabled()){
            log.debug("用户关注事件, openid:{"+openid+"},sourceId:{"+sourceId+"}");
        }
        String url = FINANCE_ACTIVE_URL;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("openId", openid);
        params.put("sourceId", sourceId);
        try{
            HttpClientUtils.post(url, params, Constants.CHARSET_UTF8);
        }catch (Exception e){
            log.error("fail to push user activity to finance", e);
        }
    }
}
