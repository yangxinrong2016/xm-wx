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
    private static final Logger log1 = Logger.getLogger("infoLog");
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
        String openid = "";
        String content = "";
        try {
            JSONObject ret = JSONObject.parseObject(postData);
            openid = ret.getString("openid");
            content = ret.getString("content");
            if(StringUtils.isEmpty(openid) || StringUtils.isEmpty(content)){
                retDTO.setCode(500);
                retDTO.setMsg("openid is null or content is null");
               String retDtoJson =  JSONUtil.toJson(retDTO);
               log1.error("fail | send | openid is null or content is null | WX_ID:"+WX_ID+" | openid:"+openid+" | content:"+content+" | errorCode:500");
                return retDtoJson;
            }
            // 异步请求
            customMsgService.sendText(WX_ID, openid, content, true);
        } catch (Exception e){
            log.error("send custom msg faile", e);
            retDTO.setCode(-2);
            retDTO.setMsg("send custom msg faile");
            String retDtoJson = JSONUtil.toJson(retDTO);
            log1.error("fail | send | send custom msg fail and exception occured | WX_ID:"+WX_ID+" | openid:"+openid+" | content:"+content+" | errorCode:-2");
            return retDtoJson;
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
        String openid = "";
        String content = "";
        String templateMsg = "";
        try {
            JSONObject ret = JSONObject.parseObject(postData);
            openid = ret.getString("openid");
            content = ret.getString("content");
            templateMsg = ret.getString("templateMsg");
            if(StringUtils.isEmpty(openid) || StringUtils.isEmpty(content)){
                retDTO.setCode(500);
                retDTO.setMsg("openid is null or content is null");
                log1.error("fail | sendTAndTemplateMsg | openid is null or content is null | openid:"+openid+" | content:"+content+" | errorCode:500");
                return JSONUtil.toJson(retDTO);
            }
            // 异步请求
            customMsgService.sendTextAndTemplateMsg(WX_ID, openid, content, templateMsg);
        } catch (Exception e){
            log.error("send custom msg faile", e);
            retDTO.setCode(-2);
            retDTO.setMsg("send custom msg faile");
            log1.error("fail | sendTAndTemplateMsg | send custom msg faile | openid:"+openid+" | content:"+content+" | errorCode:-2");
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(0);
        retDTO.setMsg("成功");
        log1.info("success | sendTAndTemplateMsg | send success | openid:"+openid+" | content:"+content+" | errorCode:0");
        return JSONUtil.toJson(retDTO);
    }
    
}
