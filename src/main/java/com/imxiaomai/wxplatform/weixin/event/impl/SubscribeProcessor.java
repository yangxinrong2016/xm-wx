package com.imxiaomai.wxplatform.weixin.event.impl;

import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.WxUser;
import com.imxiaomai.wxplatform.service.ITemplateMsgService;
import com.imxiaomai.wxplatform.service.IWxUserService;
import com.imxiaomai.wxplatform.util.Xml;
import com.imxiaomai.wxplatform.weixin.event.IEventProcessor;
import com.imxiaomai.wxplatform.weixin.service.P2PService;
import com.imxiaomai.wxplatform.weixin.service.TextMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("subscribeProcessor")
public class SubscribeProcessor implements IEventProcessor {
    private static Logger log = LoggerFactory.getLogger(SubscribeProcessor.class);
    @Resource
    P2PService p2PService;
    @Resource
    TextMsgService textMsgService;
    @Resource
    ITemplateMsgService templateMsgServiceImpl;
    @Resource
    IWxUserService wxUserServiceImpl;

    @Override
    public String process(String wxId, String openid, long createTime, Xml.ParsedXml parsedXml) {
        //订阅
        String key = parsedXml.text("EventKey");
        String ticket = parsedXml.text("Ticket");
        int source = 0;
        if(!key.equals("")){
            //通过扫描二维码关注的
            try {
                source = new Integer(key.replaceAll("qrscene_", ""));
            } catch (NumberFormatException e) {
                log.error("扫描的二维码链接不符合标注，没有场景id, key:{}", key);
            }
            log.debug("通过扫描二维码关注, wxId:{"+wxId+"}, openid:{"+openid+"}, key:{"+key+"}, ticket:{"+ticket+"}, source:{"+source+"}");
            p2PService.pushUserActivity(openid, "扫码关注", "来源:"+source);
        }else {
            p2PService.pushUserActivity(openid, "关注", "");
        }
        WxUser wxUser = wxUserServiceImpl.getByWxIdAndOpenid(wxId, openid);
        if(null == wxUser){
            //还没有此用户信息，新增此用户信息
            wxUser = new WxUser();
            wxUser.setWxid(wxId);
            wxUser.setOpenid(openid);
            wxUser.setIssubscribe((byte) Constants.WX_USER_IS_SUBSCRIBE);
            wxUser.setCountsubscribe(1);
            if(!key.equals("")){
                //通过扫描二维码关注的
                wxUser.setSource(source);
                wxUser.setTicket(ticket);
            }else{
                wxUser.setSource(0);
                wxUser.setTicket("");
            }
            wxUserServiceImpl.insert(wxUser);
        }else {
            //更新订阅状态和次数
            wxUser.setIssubscribe((byte) Constants.WX_USER_IS_SUBSCRIBE);
            wxUser.setCountsubscribe(wxUser.getCountsubscribe()+1);
            if(!key.equals("")){
                //通过扫描二维码关注的
                //需求 #208，如果原来有source，就不要更新了，以第一次为准
                if(0 == wxUser.getSource()){
                    wxUser.setSource(source);
                    wxUser.setTicket(ticket);
                }
            }else{
                wxUser.setSource(0);
                wxUser.setTicket("");
            }
            //TODO:2014.12.26 tangwei 插入四字节的emoji还是有问题，问题在java代码层/mysql驱动层。因为由脚本每天跑而且现在也不需要使用nickname，所以暂时不插入
            wxUser.setNickname("");
            wxUserServiceImpl.update(wxUser);
        }
        try{
            return textMsgService.getResponseContent(wxId, openid, createTime, "关注回复");
        }catch (Exception e){
            log.error("SubscribeProcessor execute failed", e);
            return "";
        }

    }
}
