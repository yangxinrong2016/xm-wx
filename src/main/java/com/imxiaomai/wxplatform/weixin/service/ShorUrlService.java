package com.imxiaomai.wxplatform.weixin.service;

import com.imxiaomai.wxplatform.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zengyaowen on 15/11/25.
 */
@Service("shorUrlService")
public class ShorUrlService {
    Logger logger = LoggerFactory.getLogger(ShorUrlService.class);
    @Value("${codepay.url}")
    private String codePayUrl;
    private Map<String, String> urlMap = new HashMap<>();

    @PostConstruct
    private void postConstruct(){
        urlMap.put(Constants.WEI_XIN_FOWARD_GRAP, "http://m.m.mall.test.imxiaomai.com/forward/grap");
        urlMap.put(Constants.MALL_NEW_VERSION, codePayUrl);
    }

    public String getUrl(String shortName){
        if(urlMap.containsKey(shortName)){
            return urlMap.get(shortName);
        }
        return null;
    }
}
