package com.imxiaomai.wxplatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.Menu;
import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.service.IMenuService;
import com.imxiaomai.wxplatform.util.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-13.
 */
@Controller
@RequestMapping(value = "/test")
public class TestController {
    @Resource
    IMenuService menuServiceImpl;
    @Value("${wxid}")
    public String WX_ID;
    @RequestMapping(value = "/list")
    @ResponseBody
    public String list(){
        return "Success";
    }

    @RequestMapping(value = "/db")
    @ResponseBody
    public String menu(){
        Menu menu = menuServiceImpl.getMenuByWxId(WX_ID);
        return menu.getContent().toString();
    }

    @RequestMapping(value = "/object")
    @ResponseBody
    public Object returnObj(){
        RetDTO retDTO = new RetDTO();
        retDTO.setCode(-2);
        retDTO.setMsg("send custom msg faile");
        return JSONUtil.toJson(retDTO);
    }
}
