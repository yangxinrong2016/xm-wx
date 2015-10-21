package com.imxiaomai.wxplatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.JsapiTicket;
import com.imxiaomai.wxplatform.dto.JsApiConfigDTO;
import com.imxiaomai.wxplatform.dto.RetDTO;
import com.imxiaomai.wxplatform.service.IJsApiTicketService;
import com.imxiaomai.wxplatform.util.HttpReqUtils;
import com.imxiaomai.wxplatform.util.JSONUtil;
import com.imxiaomai.wxplatform.util.SHA1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/**
 * 微信jsapi接口
 * @author rainbow
 *
 */
@Controller
@RequestMapping("/jsapi")
public class JsApiController {
    
    private static final Logger log = LoggerFactory.getLogger(JsApiController.class);
	@Resource
	IJsApiTicketService JsApiTicketServiceImpl;
	@Value("${app.id}")
	public String APP_ID;
	@Value("${wxid}")
	public String WX_ID;
    /**
     * 
     * 获取权限验证config
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/config")
    @ResponseBody
    public Object getConfig(HttpServletRequest request, HttpServletResponse response) throws IOException  {
        long start = System.currentTimeMillis();
        String postData = HttpReqUtils.getDataFromRequest(request);
		RetDTO retDTO = new RetDTO();
        try {
            JSONObject postJsonNode = JSONObject.parseObject(postData);
            String url = postJsonNode.getString("url") == null ? "" : postJsonNode.getString("url");
        	String timestamp = start+"";
        	String nonceStr = "xm_wx_js"+getRandomString(5);
    		// 获取jsapi调用的ticket。
    		JsapiTicket jsApiTicket = JsApiTicketServiceImpl.getByWxId(WX_ID);
    		if(jsApiTicket==null) {
				log.error("get js api ticket error, the wei xin id is :" + WX_ID);
				retDTO.setCode(-1);
				retDTO.setMsg("get jsapi ticket fail because the jspApiTicket is null");
				return retDTO;
    		}
    		String ticket = jsApiTicket.getTicket();
    		String signature= "jsapi_ticket="+ticket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
    		signature = SHA1.gen(signature);
    		JsApiConfigDTO jsApiConfig = new JsApiConfigDTO();
    		jsApiConfig.appId = APP_ID;
    		jsApiConfig.timestamp = timestamp;
    		jsApiConfig.nonceStr = nonceStr;
    		jsApiConfig.signature  = signature;
			retDTO.setCode(0);
			retDTO.setMsg("成功");
			retDTO.setData(jsApiConfig);
			return JSONUtil.toJson(retDTO);
        } catch (Exception e) {
//            AutoLogs.log(AutoLogs.LOG_TYPE_IOEXCEPTION, (int)(System.currentTimeMillis() - start));
			log.error("get js api signature error", e);
			retDTO.setCode(-1);
			retDTO.setMsg("get jsapi ticket becasue exception");
			return JSONUtil.toJson(retDTO);
        }
    }
    
	private static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }
	    return sb.toString();     
	 }

}


