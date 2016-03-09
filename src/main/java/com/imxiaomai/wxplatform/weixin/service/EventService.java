package com.imxiaomai.wxplatform.weixin.service;

import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.TemplateMsg;
import com.imxiaomai.wxplatform.domain.WxUser;
import com.imxiaomai.wxplatform.service.ITemplateMsgService;
import com.imxiaomai.wxplatform.service.IWxUserService;
import com.imxiaomai.wxplatform.util.Xml;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("eventService")
public class EventService {

    private static final Logger log = Logger.getLogger(EventService.class);
    @Resource
    P2PService p2PService;
    @Resource
    TextMsgService textMsgService;
    @Resource
    ITemplateMsgService templateMsgServiceImpl;
    @Resource
    IWxUserService wxUserServiceImpl;
    @Resource
    FinanceService financeService;
    /**
     * 处理微信推送的事件
     * 
     * @throws WXException 
     */
    public String handleEventPush(String wxId, String openid, long createTime, Xml.ParsedXml parsedXml) throws WXException {

        String retString = "";
        String key = "";
        WxUser wxUser = null;
        int source = 0;
        try {
            String event = parsedXml.text("Event");
            switch (event) {
            case "TEMPLATESENDJOBFINISH":
                //模板消息发送后的推送
                String msgId = parsedXml.text("MsgID");
                String status = parsedXml.text("Status");
                TemplateMsg tplMsg = templateMsgServiceImpl.getByWxIdAndOpenIdAndMsgId(wxId, openid, msgId);
                log.debug("更新模板消息的到达状态数据, msgId:{"+msgId+"}");
                tplMsg.setEventstatus(status);
                templateMsgServiceImpl.update(tplMsg);
                break;
            case "subscribe":
                //订阅
                if(log.isDebugEnabled()){
                    log.debug("新用户关注, wxId:{"+wxId+"}, openid:{"+openid+"}");
                }
                key = parsedXml.text("EventKey");
                String ticket = parsedXml.text("Ticket");
                source = 0;
                if(!key.equals("")){
                    //通过扫描二维码关注的
                    try {
                        source = new Integer(key.replaceAll("qrscene_", ""));
                    } catch (NumberFormatException e) {
                        log.error("扫描的二维码链接不符合标注，没有场景id, key:{"+key+"}");
                    }
                    log.debug("通过扫描二维码关注, wxId:{"+wxId+"}, openid:{"+openid+"}, key:{"+key+"}, ticket:{"+ticket+"}, source:{"+source+"}");
                    p2PService.pushUserActivity(openid, "扫码关注", "来源:"+source);
                    financeService.pushUserActivity(openid, source);
                }else {
                    p2PService.pushUserActivity(openid, "关注", "");
                }
                wxUser = wxUserServiceImpl.getByWxIdAndOpenid(wxId, openid);
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
                log.debug("关注事件自动回复:{"+retString+"}");
                retString = textMsgService.getResponseContent(wxId, openid, createTime, "关注回复");
                break;
            case "unsubscribe":
                //取消订阅
                log.debug("取消关注, wxId:{"+wxId+"}, openid:{"+openid+"}");
                p2PService.pushUserActivity(openid, "取消关注", "");

                wxUser = wxUserServiceImpl.getByWxIdAndOpenid(wxId, openid);
                if(wxUser == null){
                    //比较异常的情况：还没有此用户信息
                    log.error("非关注用户取消关注");
                    wxUser = new WxUser();
                    wxUser.setWxid(wxId);
                    wxUser.setOpenid(openid);
                    wxUser.setIssubscribe((byte) Constants.WX_USER_IS_SUBSCRIBE);
                    wxUser.setCountsubscribe(1);
                    wxUser.setCity("");
                    wxUser.setCountry("");
                    wxUser.setHeadimgurl("");
                    wxUser.setProvince("");
                    wxUser.setNickname("");
                    wxUser.setSex("");
                    wxUser.setUnionid("");
                    wxUser.setSource(0);
                    wxUser.setTicket("");
                    wxUserServiceImpl.insert(wxUser);
                }else {
                    //更新订阅状态和次数
                    wxUser.setIssubscribe((byte)Constants.WX_USER_NOT_SUBSCRIBE);
                    //TODO:2014.12.26 tangwei 插入四字节的emoji还是有问题，问题在java代码层/mysql驱动层。因为由脚本每天跑而且现在也不需要使用nickname，所以暂时不插入
                    wxUser.setNickname("");
                    wxUserServiceImpl.update(wxUser);
                }
                break;

            case "CLICK":
                //自定义菜单点击推送事件
                key = parsedXml.text("EventKey");
                log.debug("点击自定义菜单, wxId:{"+key+"}, openid:{"+openid+"}, key:{"+key+"}");
                p2PService.pushUserActivity(openid, "点击自定义菜单", "key:"+key);
                if(key.equals("CLICK_MY_EXPRESS")){
                    //点击了自定义菜单中“我的快递”
                    retString = p2PService.clickMenuResponse(wxId, openid);
                }
                break;
            case "SCAN":
                //已关注用户扫码
                key = parsedXml.text("EventKey");
                ticket = parsedXml.text("Ticket");
                try {
                    source = new Integer(key.replaceAll("qrscene_", ""));
                } catch (NumberFormatException e) {
                    log.error("扫描的二维码链接不符合标注，没有场景id, key:{"+key+"}");
                }
                log.debug("已关注用户扫码, wxId:{"+wxId+"}, openid:{"+openid+"}, key:{"+key+"}, ticket:{"+ticket+"}, source:{"+source+"}");
                p2PService.pushUserActivity(openid, "已关注用户扫码", "来源:"+source);
                if(source > 0){
                    //从渠道导入的流量才通知金融，用于金融建立用户和门店的关联关系
                    financeService.pushUserActivity(openid, source);
                }
                break;
            case "LOCATION":
                //上报地理位置事件
                String lat = parsedXml.text("Latitude");
                String lng = parsedXml.text("Longitude");
                String precision = parsedXml.text("Precision");
                log.debug("上报地理位置，lat:{"+lat+"}, lng:{"+lng+"}, precision:{"+precision+"}");
                p2PService.pushUserActivity(openid, "上报地理位置", "lat:"+lat+",lng:"+lng+",precision:"+precision);
                break;
            case "VIEW":
                //点击菜单链接
                key = parsedXml.text("EventKey");
                log.debug("点击自定义菜单, wxId:{"+wxId+"}, openid:{"+openid+"}, key:{"+key+"}");
                p2PService.pushUserActivity(openid, "点击自定义菜单", "key:"+key);
                break;
            default:
                log.debug("未处理的事件类型:{"+event+"}");
                break;
            }
        }
//        catch (SQLException e) {
//            log.error("sql error:"+e.getMessage());
//            e.printStackTrace();
//            throw new WXException("sql error:"+e.getMessage());
//        } catch (UnsupportedEncodingException e) {
//            log.error("不支持的编码方式:"+e.getMessage());
//            e.printStackTrace();
//            throw new WXException("不支持的编码方式:"+e.getMessage());
//        } catch (IOException e) {
//            log.error("解析json字符串出错:"+e.getMessage());
//            e.printStackTrace();
//            throw new WXException("解析json字符串出错:"+e.getMessage());
//        }
        catch (Exception e){
            log.error("handle event failed, openid is : " + openid + "parsedXml is : " + parsedXml );
            return "";
        }
        return retString;
        
    }
}
