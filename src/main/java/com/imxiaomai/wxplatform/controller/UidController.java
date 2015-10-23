package com.imxiaomai.wxplatform.controller;

import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.util.JSONUtil;
import com.imxiaomai.wxplatform.weixin.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zengyaowen on 15-10-20.
 */

@Controller
@RequestMapping("/uid")
public class UidController {

    @Resource
    UserService userService;
    @Value("${app.id}")
    public String APP_ID;
    @Value("${app.secret}")
    public String APP_SECRET;
    @Value("${wxid}")
    public String WXID;
    @RequestMapping("/getByAppOpenId")
    @ResponseBody
    public Object getByOpenId(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(required=false, defaultValue="") String openId) throws IOException {
        RetDTO retDTO= new RetDTO();
        if(StringUtils.isEmpty(openId)){
            retDTO.setCode(500);
            retDTO.setMsg("openId is empty");
            return JSONUtil.toJson(retDTO);
        }
        String uid = userService.getUidByOpenId(openId, WXID, APP_ID, APP_SECRET);
        if(StringUtils.isNotEmpty(uid)){
            retDTO.setCode(200);
            retDTO.setMsg("成功获取uid");
            retDTO.setData(uid);
            return JSONUtil.toJson(retDTO);
        }
        retDTO.setCode(500);
        retDTO.setMsg("get unionId empty");
        return JSONUtil.toJson(retDTO);
    }
}
