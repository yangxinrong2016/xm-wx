package com.imxiaomai.wxplatform.controller;

import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.weixin.service.UidService;
import org.apache.commons.lang.StringUtils;
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
    UidService uidService;
    @RequestMapping("/getByOpenId")
    @ResponseBody
    public Object send(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(required=false, defaultValue="") String openId) throws IOException {
        RetDTO retDTO= new RetDTO();
        if(StringUtils.isEmpty(openId)){
            retDTO.setCode(500);
            retDTO.setMsg("openId is empty");
            return retDTO;
        }
        String uid = uidService.getUidByOpenId(openId);
        retDTO.setCode(200);
        retDTO.setMsg("成功获取uid");
        retDTO.setData(uid);
        return retDTO;

    }
}
