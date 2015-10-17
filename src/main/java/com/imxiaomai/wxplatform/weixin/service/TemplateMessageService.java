package com.imxiaomai.wxplatform.weixin.service;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.common.WXErrorConstants;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.domain.TemplateMsg;
import com.imxiaomai.wxplatform.service.ITemplateMsgService;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import com.imxiaomai.wxplatform.util.Md5Util;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("templateMessageService")
public class TemplateMessageService {
    
    private static final Logger log = LoggerFactory.getLogger(TemplateMessageService.class);
    @Resource
    ITemplateMsgService templateMsgService;
    @Resource
    AccessTokenService accessTokenService;
    public static final Integer MAX_RETRY = 3;
    public static final int ERROR_CODE_WAIT_SEND = -9991;
    public static final int ERROR_CODE_SEND_TO_WX_FAIL = -9992;
    public static final int ERROR_CODE_RESEND_SUCESS = -9990;

    public static final Map<Integer, String> ERROR_MSG_MAP = new HashMap<Integer, String>();
    static {
        ERROR_MSG_MAP.put(ERROR_CODE_WAIT_SEND, "等待发送给微信");
        ERROR_MSG_MAP.put(ERROR_CODE_SEND_TO_WX_FAIL, "发送给微信失败");
        ERROR_MSG_MAP.put(ERROR_CODE_RESEND_SUCESS, "重新发送成功");
    }

    public static String getErrorCodeMsg(int errorCode) {
        String msg = ERROR_MSG_MAP.get(errorCode);
        return msg == null ? "未知错误码" : msg;
    }

    /**
     * 发送模板消息，默认是异步发送
     *
     * @param msgJsonString
     * @param openid
     * @param wxId
     */
    public void send(final String msgJsonString, final String openid, final String wxId) {
        try {
            syncSend(msgJsonString, openid, wxId,MAX_RETRY);
        } catch (WXException e) {
            log.error("send template message failed");
        }
    }

    public boolean syncSend(final String msgJsonString, final String openid, final String wxId, final Integer retry) throws WXException{

        long start = System.currentTimeMillis();
        try {
            String url = Constants.TEMPLATE_SEND_URL;
            //先将要发送的模板消息入库
            Date now = new Date();
            TemplateMsg tplMsg = new TemplateMsg();
            tplMsg.setWxid(wxId);
            tplMsg.setOpenid(openid);
            tplMsg.setMsg(msgJsonString);
            tplMsg.setMsghash(Md5Util.md5AsLowerHex(msgJsonString));
            tplMsg.setMsgid("");
            tplMsg.setErrorcode(ERROR_CODE_WAIT_SEND);
            tplMsg.setCodemsg(getErrorCodeMsg(ERROR_CODE_WAIT_SEND));
            tplMsg.setErrmsg("");
            tplMsg.setCreatetime(now);
            tplMsg.setUpdatetime(now);
            tplMsg.setEventstatus("");
            //此消息的当前已经重试次数，可以动态变化来控制最大重发次数
            tplMsg.setRetry(MAX_RETRY - retry);
            int tplMsgId = templateMsgService.insert(tplMsg);

            //发送模板消息
            AccessToken accessToken = accessTokenService.getAccessToken(wxId);
            url = String.format(url, accessToken);
            String retStr = HttpClientUtils.post(url, msgJsonString, Constants.CHARSET_UTF8);
            if (StringUtils.isEmpty(retStr)) {
                //发送给微信失败
                tplMsg = templateMsgService.getById(tplMsgId);
                tplMsg.setErrorcode(ERROR_CODE_SEND_TO_WX_FAIL);
                tplMsg.setErrmsg(getErrorCodeMsg(ERROR_CODE_SEND_TO_WX_FAIL));
                templateMsgService.update(tplMsg);
                log.error("访问微信接口失败, 接口:{}, 返回值:{}", url, retStr);
                throw new WXException(String.format("访问微信接口失败, 接口:%s, 返回值:%s", url, retStr));
            }

            JSONObject responseJsonNode = JSONObject.parseObject(retStr);
            int errcode = responseJsonNode.getInteger("errcode") == null ? -999999 : responseJsonNode.getInteger("errcode");
            String errmsg = responseJsonNode.getString("errmsg") == null ? "" : responseJsonNode.getString("errmsg");
            String msgid = responseJsonNode.getString("msgid") == null ? "" : responseJsonNode.getString("msgid");
            tplMsg = templateMsgService.getById(tplMsgId);
            if(tplMsg == null){
                log.error("没有找到此模板消息, tplMsgId:{}", tplMsgId);
                throw new WXException("没有找到此模板消息, tplMsgId:"+tplMsgId);
            }
            tplMsg.setErrorcode(errcode);
            tplMsg.setCodemsg(WXErrorConstants.getErrorMsg(errcode));
            tplMsg.setErrmsg(errmsg);
            tplMsg.setMsgid(msgid);
            templateMsgService.update(tplMsg);
            if (errcode != 0) {
                //发送模板失败
                log.error("调用模板消息接口失败, tplMsgId:{"+tplMsgId+"}, errcode:{"+errcode+"}, errmsg:{"+WXErrorConstants.getErrorMsg(errcode)+"}");
                throw new WXException("调用模板消息接口失败, " + retStr);
            }

        } catch (Exception e) {
            log.error("syncSend failed", e);
            return false;
        }
//        catch (IOException e) {
//            log.error("微信返回数据解析异常, 接口:{}, 返回码:{}, 返回值:{}", api, httpCode, response);
//            e.printStackTrace();
//            AutoLogs.log(AutoLogs.LOG_TYPE_IOEXCEPTION, (int) (System.currentTimeMillis() - start));
//            return false;
//        } catch (SQLException e) {
//            log.error("数据库异常，" + e.getMessage());
//            e.printStackTrace();
//            AutoLogs.log(AutoLogs.LOG_TYPE_SQL_EXCEPTION, (int) (System.currentTimeMillis() - start));
//            return false;
//        } catch (WXException e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//            AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_TEMPLATEMSG, (int) (System.currentTimeMillis() - start));
//            return false;
//        } catch (Exception e) {
//            log.error("send template failed", e);
//        }

//        AutoLogs.log(AutoLogs.LOG_TYPE_WX_SEND_TEMPLATE_MSG, (int) (System.currentTimeMillis() - start));

        return true;
    }

    /**
     * 将指定模板消息的重新发送一遍，同步发送
     */
    public boolean resendFailed(Integer tplMsgId) {

        long start = System.currentTimeMillis();
        boolean isSuccess = false;
        try {
            TemplateMsg tplMsg = templateMsgService.getById(tplMsgId);
            if(tplMsg != null && tplMsg.getErrorcode() != 0 && tplMsg.getRetry() < MAX_RETRY){
                //重发失败消息生成的新的消息（对我们来说是同一条，但是对于微信来说是不同的消息），如果失败，也不能进行重试。
                //否则，将会引起重发风暴，可能死循环。
                isSuccess = syncSend(tplMsg.getMsg(), tplMsg.getOpenid(), tplMsg.getWxid(), -1000);
                if(isSuccess){
                    tplMsg.setErrorcode(ERROR_CODE_RESEND_SUCESS);
                    tplMsg.setCodemsg(getErrorCodeMsg(ERROR_CODE_RESEND_SUCESS));
                }
                tplMsg.setRetry(tplMsg.getRetry() + 1);
                templateMsgService.update(tplMsg);
            }else {
                log.error("重发失败的模板消息出错, tplMsgId:{}", tplMsgId);
            }

        } catch (Exception e) {
            log.error("resend failed", e);
            //AutoLogs.log(AutoLogs.LOG_TYPE_SQL_EXCEPTION, (int) (System.currentTimeMillis() - start));
        }
        return isSuccess;
    }
    
}
