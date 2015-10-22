package com.imxiaomai.wxplatform.controller;

import com.imxiaomai.wxplatform.util.HttpReqUtils;
import com.imxiaomai.wxplatform.util.SHA1;
import com.imxiaomai.wxplatform.util.Xml;
import com.imxiaomai.wxplatform.weixin.service.EventService;
import com.imxiaomai.wxplatform.weixin.service.P2PService;
import com.imxiaomai.wxplatform.weixin.service.TextMsgService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
/**
 * Created by zengyaowen on 15-10-13.
 */
@Controller
public class EntryController {

    private static final Logger log = Logger.getLogger(EntryController.class);
    @Resource
    TextMsgService textMsgService;
    @Resource
    P2PService p2PService;
    @Resource
    EventService eventService;
    @Value("${token}")
    public String TOKEN;
    /**
     * 验证初始化请求
     *
     * @param request
     * @param response
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @RequestMapping(value="/entry", method= RequestMethod.GET)
    @ResponseBody
    public Object validate(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(required=false, defaultValue="") String signature,
                           @RequestParam(required=false, defaultValue="") String timestamp,
                           @RequestParam(required=false, defaultValue="") String nonce,
                           @RequestParam(required=false, defaultValue="") String echostr) {

        long start = System.currentTimeMillis();
        if(log.isDebugEnabled()){
            log.debug("/validate, signature:{"+signature+"}, timestamp:{"+ timestamp+"},nonce:{"+nonce+"}, echostr:{"+echostr+"}");
        }
        String tmpString = "";
        try {
            tmpString = SHA1.gen(TOKEN, timestamp, nonce);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            log.error("没有SHA1算法",e);
            //AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_ENTRY_GET, (int) (System.currentTimeMillis() - start));
        }
        if(log.isDebugEnabled()){
            log.debug("验证微信字符串, 微信:{"+signature+"}, 计算得到:{"+tmpString+"}");
        }
        if(tmpString.equals(signature)){
            return echostr;
        }else {
            return "false";
        }

    }

    /**
     * 接收消息推送的接口
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/entry", method=RequestMethod.POST)
    @ResponseBody
    public Object entry(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(required=false, defaultValue="") String signature,
                        @RequestParam(required=false, defaultValue="") String timestamp,
                        @RequestParam(required=false, defaultValue="") String nonce,
                        @RequestParam(required=false, defaultValue="") String echostr) throws IOException {
        long start = System.currentTimeMillis();
        String postData = HttpReqUtils.getDataFromRequest(request);
        String ret = "";
        try {
            String tmpString = SHA1.gen(TOKEN, timestamp, nonce);
            log.debug("验证微信字符串, 微信:{"+signature+"}, 计算得到:{"+tmpString+"}");
            if(tmpString.equals(signature)){
                Xml.ParsedXml parsedXml = Xml.of(postData);
                String toUser = parsedXml.text("ToUserName");
                String fromUser = parsedXml.text("FromUserName");
                long createTime = new Long(parsedXml.text("CreateTime"));
                String msgType = parsedXml.text("MsgType");
                switch (msgType) {
                    case "text":
                        ret = textMsgService.getResponseContent(toUser, fromUser, createTime, parsedXml);
                        p2PService.pushUserActivity(fromUser, "用户发送文本", "");
                        break;
                    case "image":
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        p2PService.pushUserActivity(fromUser, "用户发送图片", "");
                        break;
                    case "voice":
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        p2PService.pushUserActivity(fromUser, "用户发送音频", "");
                        break;
                    case "video":
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        p2PService.pushUserActivity(fromUser, "用户发送视频", "");
                        break;
                    case "location":
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        p2PService.pushUserActivity(fromUser, "用户发送地理位置", "");
                        break;
                    case "link":
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        p2PService.pushUserActivity(fromUser, "用户发送链接", "");
                        break;
                    case "event":
                        ret = eventService.handleEventPush(toUser, fromUser, createTime, parsedXml);
                        break;
                    default:
                        log.debug("未知的消息类型:{"+msgType+"}");
                        //全是默认回复
                        ret = textMsgService.getDefaultResponseContent(toUser, fromUser, createTime);
                        break;
                }
            }else {
                log.debug("验证微信字符串失败, 微信:{"+signature+"}, 计算得到:{"+tmpString+"}");
            }
        }
//        catch (NoSuchAlgorithmException e) {
//            log.error("没有SHA1算法",e);
//            //AutoLogs.log(AutoLogs.LOG_TYPE_OTHER_EXCEPTION, (int)(System.currentTimeMillis() - start));
//        } catch (DocumentException | XmlPullParserException e) {
//            log.error("解析xml失败,"+e.getMessage(),e);
//            //AutoLogs.log(AutoLogs.LOG_TYPE_OTHER_EXCEPTION, (int)(System.currentTimeMillis() - start));
//        } catch (WXException e) {
//            log.error("处理微信推送失败, toUser:{}, fromUser:{}, createTime:{}, msgType{}, "+e);
//            //AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_ENTRY_POST, (int)(System.currentTimeMillis() - start));
//        }
        catch (Exception e){
            log.error("微信自动回复失败", e);
        }
        return ret;
    }


}
