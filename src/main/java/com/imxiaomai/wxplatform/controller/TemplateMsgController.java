package com.imxiaomai.wxplatform.controller;


import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.util.HttpReqUtils;
import com.imxiaomai.wxplatform.util.JSONUtil;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import com.imxiaomai.wxplatform.weixin.service.TemplateMessageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 模板消息控制器
 * 
 * @author tomjamescn
 *
 */
@Controller
@RequestMapping("/templateMsg")
public class TemplateMsgController {


    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TemplateMsgController.class);
    private static final String RESEND_TOKEN = "oVfRGoIVrkWxD44u7DVHNIPILVR";

    @Resource
    TemplateMessageService templateMessageService;
    @Value("${wxid}")
    public String WX_ID;
    /**
     * 
     * @return
     */
    @RequestMapping("/send")
    @ResponseBody
    public Object send(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        RetDTO retDTO = new RetDTO();
        try {
            String postData = HttpReqUtils.getDataFromRequest(request);

            log.debug("/templateMsg/send postData:{"+postData+"}");

            JSONObject postJsonNode = JSONObject.parseObject(postData);
            String openid = postJsonNode.getString("touser");
            if(StringUtils.isEmpty(openid)){
                throw new WXException("openid为空");
            }
            //异步发送
            templateMessageService.send(postData, openid, WX_ID);

        } catch (Exception e) {
            log.error("发送模板消息出现异常，", e);
//            AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_TEMPLATEMSG, (int)(System.currentTimeMillis() - start));
            retDTO.setCode(-1);
            retDTO.setMsg("发送失败");
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(0);
        retDTO.setMsg("成功接收发送请求");
        return JSONUtil.toJson(retDTO);
    }

    /**
     * 重发某个失败的模板消息
     * @param request
     * @return
     */
    @RequestMapping("/resendFailed")
    @ResponseBody
    public Object resendFailed(HttpServletRequest request) {

        long start = System.currentTimeMillis();
        boolean isSuccess = false;
        RetDTO retDTO = new RetDTO();
        try{
            String postData = HttpReqUtils.getDataFromRequest(request);
            JSONObject postJsonNode = JSONObject.parseObject(postData);
            String token = postJsonNode.getString("token") == null ? "" : postJsonNode.getString("token");
            Long tplMsgId = postJsonNode.getLong("tplMsgId") == null ? 0L : postJsonNode.getLong("tplMsgId");

            if(!token.equals(RESEND_TOKEN) || tplMsgId == 0L){
                retDTO.setCode(500);
                retDTO.setMsg("非法访问");
                return JSONUtil.toJson(retDTO);
            }
            isSuccess = templateMessageService.resendFailed(tplMsgId.intValue());
        }catch (Exception e) {
            log.error("重发失败，", e);
            retDTO.setCode(-1);
            retDTO.setMsg("重发失败");
            return JSONUtil.toJson(retDTO);
        }
        if(!isSuccess){
            retDTO.setCode(-1);
            retDTO.setMsg("重发失败");
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(0);
        retDTO.setMsg("成功");
        return JSONUtil.toJson(retDTO);
    }
    
    
}
