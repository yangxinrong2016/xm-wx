package com.imxiaomai.wxplatform.weixin.service;


import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.common.WXErrorConstants;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.domain.CustomMsg;
import com.imxiaomai.wxplatform.service.ICustomMsgServcie;
import com.imxiaomai.wxplatform.service.ITemplateMsgService;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import com.imxiaomai.wxplatform.util.Md5Util;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("customMsgService")
public class CustomMsgService {

    @Resource
    AccessTokenService accessTokenService;
    @Resource
    ICustomMsgServcie customServiceImpl;
    @Resource
    TemplateMessageService templateMsgService;

    private static final Logger log = Logger.getLogger(CustomMsgService.class);

    public static final long MAX_RETRY = 3;

    public static final int ERROR_CODE_WAIT_SEND = -9991;
    public static final int ERROR_CODE_SEND_TO_WX_FAIL = -9992;

    public static final Map<Integer, String> ERROR_MSG_MAP = new HashMap<Integer, String>();

    static {
        ERROR_MSG_MAP.put(ERROR_CODE_WAIT_SEND, "等待发送给微信");
        ERROR_MSG_MAP.put(ERROR_CODE_SEND_TO_WX_FAIL, "发送给微信失败");
    }

    public static String getErrorCodeMsg(int errorCode) {
        String msg = ERROR_MSG_MAP.get(errorCode);
        return msg == null ? "未知错误码" : msg;
    }

    public void sendTextAndTemplateMsg(final String wxId, String openid, String textContent, String templateMsg){
        int errorcode = WXErrorConstants.SUCCESS;
        try {
            errorcode = this.sendText(wxId, openid, textContent, true);
        } catch (Exception e) {
            log.error("send text msg failed", e);
        }
        if(WXErrorConstants.SUCCESS != errorcode){
            templateMsgService.send(templateMsg, openid, wxId);
        }


    }

    public int sendText(final String wxId, final String openid,
                        final String content, final boolean accessTokenExpiredRetry){
        int errcode = sendText(wxId, openid, content);
        if(accessTokenExpiredRetry && (errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_EXPIRED
                || errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_ERROR || errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_INVALID)){
            accessTokenService.refreshAccessToken(wxId);
            return sendText(wxId, openid, content);
        }else{
            return errcode;
        }
    }

    public int sendText(final String wxId, final String openid,
                         final String content){
        long start = System.currentTimeMillis();
        String url = Constants.CUSTOM_MSGSEND_URL;
        AccessToken accessToken = null;
        try {
            accessToken = accessTokenService.getAccessToken(wxId);
        } catch (Exception e) {
            log.error("/sendText get accessToken failed", e);
            return WXErrorConstants.ERROR_NO_ACCESS_TOKEN_ERROR;
        }
        // 创建文本客服消息
        String textContent = buildTextContent(openid, content);
        // 先将要发送的模板消息入库
        Date now = new Date();
        CustomMsg ctsMsg = saveCustomMsg(wxId, openid, content, now);
        Integer ctsMsgId = ctsMsg.getId();
        url = String.format(url, accessToken.getAccesstoken());
        String retStr = null;
        try {
            retStr = HttpClientUtils.post(url, textContent, Constants.CHARSET_UTF8);
        } catch (Exception e) {
            log.error("微信接口:{" + url + "} 发送客服文本消息失败", e);
            ctsMsg = customServiceImpl.getById(ctsMsgId);
            ctsMsg.setErrorcode(ERROR_CODE_SEND_TO_WX_FAIL);
            ctsMsg.setErrmsg(getErrorCodeMsg(ERROR_CODE_SEND_TO_WX_FAIL));
            customServiceImpl.update(ctsMsg);
            return ERROR_CODE_SEND_TO_WX_FAIL;
        }
        JSONObject retJson = JSONObject.parseObject(retStr);
        int errcode = retJson.get("errcode") == null ? -999999 : retJson.getIntValue("errcode");
        String errmsg = retJson.getString("errmsg");
        ctsMsg.setErrorcode(errcode);
        ctsMsg.setCodemsg(WXErrorConstants.getErrorMsg(errcode));
        ctsMsg.setErrmsg(errmsg);
        customServiceImpl.update(ctsMsg);
        return errcode;

    }

    private CustomMsg saveCustomMsg(String wxId, String openid, String content, Date now) {
        CustomMsg ctsMsg = new CustomMsg();
        ctsMsg.setWxid(wxId);
        ctsMsg.setOpenid(openid);
        ctsMsg.setMsg(content);
        ctsMsg.setMsghash(Md5Util.md5AsLowerHex(content));
        ctsMsg.setMsgtype("text");
        ctsMsg.setErrorcode(ERROR_CODE_WAIT_SEND);
        ctsMsg.setCodemsg(getErrorCodeMsg(ERROR_CODE_WAIT_SEND));
        ctsMsg.setErrmsg("");
        ctsMsg.setCreatetime(now);
        ctsMsg.setUpdatetime(now);
        ctsMsg.setRetry(0);
        customServiceImpl.insert(ctsMsg);
        return ctsMsg;
    }

    private String buildTextContent(String openid, String content) {
        JSONObject textContentJson = new JSONObject();
        textContentJson.put("content", content);
        JSONObject textJson = new JSONObject();
        textJson.put("touser", openid);
        textJson.put("msgtype", "text");
        textJson.put("text", textContentJson);
        String textContent = textJson.toString();
        log.debug("【文本】客服消息内容:{" + textContent + "}");
        return textContent;
    }
}
