package com.imxiaomai.wxplatform.weixin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.util.EscapeUtil;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import com.imxiaomai.wxplatform.util.ResourceUtil;
import com.imxiaomai.wxplatform.util.StreamUtil;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service("p2PService")
public class P2PService {
    private static final Logger log = LoggerFactory.getLogger(P2PService.class);

    /**
     * 推送用户活跃事件给p2p
     *
     * @param openid
     * @param event
     * @param msg
     */
    public void pushUserActivity(String openid, String event, String msg){
        if(log.isDebugEnabled()){
            log.debug("用户活跃事件, openid:{"+openid+"}, event:{"+event+"}, msg:{"+msg+"}");
        }
        String url = Constants.P2P_ACTIVE_URL;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("openid", openid);
        params.put("event", event);
        try{
            HttpClientUtils.post(url, params, Constants.CHARSET_UTF8);
        }catch (Exception e){
            log.error("fail to push user activity to p2p", e);
        }
    }

    /**
     * 推送点击自定义菜单给p2p
     *
     * @param wxId
     * @param openid
     * @return
     */
    public String clickMenuResponse(String wxId, String openid){
        String retStr = "";
        String retString = "";
        String url = Constants.P2P_GETMSG_URL;
        url = String.format(url, openid);
        try {
            retStr = HttpClientUtils.get(url);
        }catch (Exception e){
            log.error("fail to pass click menu event to p2p", e);
        }
        if(StringUtils.isEmpty(retStr)){
            return "";
        }

        JSONObject ret = JSONObject.parseObject(retStr);
        try{
            int code = Integer.valueOf(ret.get("code").toString());
            String msg = ret.get("msg").toString();
            if(-1 == code){
                throw new WXException("p2p.getMsg接口参数错误, "+msg);
            }else if(1 == code){
                log.debug("p2p.getMsg接口, p2p会进行消息模板推送，"+msg);
            }else {
                //有消息需要返回
                byte[] response = ResourceUtil.classpathAsBytes("click_respond.txt");
                String responseString = new String(response,"UTF-8");
                long nowSec = System.currentTimeMillis()/1000;
                retString = String.format(responseString, openid, wxId, nowSec, msg);
                if(log.isDebugEnabled()){
                    log.debug("点击自定义菜单自动回复:"+retString );
                }
            }
            return retString;
        }catch (Exception e){
            log.error("p2p result invaild, result is :" + retStr, e);
            return "";
        }

    }
    
}
