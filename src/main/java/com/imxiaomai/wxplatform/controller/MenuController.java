package com.imxiaomai.wxplatform.controller;

/**
 * Created by zengyaowen on 15-10-14.
 */

import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.util.JSONUtil;
import com.imxiaomai.wxplatform.weixin.service.WeixinMenuService;
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
 * 自定义菜单控制器
 *
 * @author tomjamescn
 *
 */
@Controller
@RequestMapping("/menu")
public class MenuController {
    private static final Logger log = Logger.getLogger(MenuController.class);
    @Resource
    WeixinMenuService weixinMenuService;
    @Value("${wxid}")
    public String WX_ID;
    @RequestMapping("/create")
    @ResponseBody
    public Object send(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long start = System.currentTimeMillis();
        RetDTO ret = new RetDTO();
        try {
            weixinMenuService.createMenu(WX_ID, true);
        } catch (Exception e) {
            log.error("创建自定义菜单失败，",e);
            //AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_MENU, (int)(System.currentTimeMillis() - start));
            ret.setCode(Constants.ERROR_CODE);
            ret.setMsg("创建自定义菜单失败，异常信息:"+e.getMessage());
            return JSONUtil.toJson(ret);
        }
        ret.setCode(Constants.SUCCESS_CODE);
        ret.setMsg("成功");
        return JSONUtil.toJson(ret);
    }

}
