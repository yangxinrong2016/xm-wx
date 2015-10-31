package com.imxiaomai.wxplatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.util.HttpReqUtils;
import com.imxiaomai.wxplatform.util.JSONUtil;
import com.imxiaomai.wxplatform.weixin.service.CustomMsgService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 客服消息控制器
 * 
 * @author tomjamescn
 *
 */
@Controller
@RequestMapping("/customMsg")
public class CustomMsgController {

    private static final Logger log = Logger.getLogger(CustomMsgController.class);
    @Resource
    CustomMsgService customMsgService;
    @Value("${wxid}")
    public String WX_ID;
    /**
     * 
     * 发送文本消息
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/sendText")
    @ResponseBody
    public Object send(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        long start = System.currentTimeMillis();
        RetDTO retDTO = new RetDTO();
        String postData = HttpReqUtils.getDataFromRequest(request);
        if(log.isDebugEnabled()){
            log.debug("/customMsg/sendText postData:{"+postData+"}");
        }
        try {
            JSONObject ret = JSONObject.parseObject(postData);
            String openid = ret.getString("openid");
            String content = ret.getString("content");
            if(StringUtils.isEmpty(openid) || StringUtils.isEmpty(content)){
                retDTO.setCode(500);
                retDTO.setMsg("openid is null or content is null");
                return JSONUtil.toJson(retDTO);
            }
            // 异步请求
            customMsgService.sendText(WX_ID, openid, content, true);
        } catch (Exception e){
            log.error("send custom msg faile", e);
            retDTO.setCode(-2);
            retDTO.setMsg("send custom msg faile");
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(0);
        retDTO.setMsg("成功");
        return JSONUtil.toJson(retDTO);
    }

    @RequestMapping("/sendTextAndTemplateMsg")
    @ResponseBody
    public Object sendTAndTemplateMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long start = System.currentTimeMillis();
        RetDTO retDTO = new RetDTO();
        String postData = HttpReqUtils.getDataFromRequest(request);
        if(log.isDebugEnabled()){
            log.debug("/customMsg/sendTextAndTemplateMsg postData:{"+postData+"}");
        }
        try {
            JSONObject ret = JSONObject.parseObject(postData);
            String openid = ret.getString("openid");
            String content = ret.getString("content");
            String templateMsg = ret.getString("templateMsg");
            if(StringUtils.isEmpty(openid) || StringUtils.isEmpty(content)){
                retDTO.setCode(500);
                retDTO.setMsg("openid is null or content is null");
                return JSONUtil.toJson(retDTO);
            }
            // 异步请求
            customMsgService.sendTextAndTemplateMsg(WX_ID, openid, content, templateMsg);
        } catch (Exception e){
            log.error("send custom msg faile", e);
            retDTO.setCode(-2);
            retDTO.setMsg("send custom msg faile");
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(0);
        retDTO.setMsg("成功");
        return JSONUtil.toJson(retDTO);
    }
    
}
