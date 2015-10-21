package com.imxiaomai.wxplatform.weixin.service;


import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.common.WXErrorConstants;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.domain.CustomMsg;
import com.imxiaomai.wxplatform.service.ICustomMsgServcie;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import com.imxiaomai.wxplatform.util.Md5Util;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("uidService")
public class UidService {

	@Resource
	AccessTokenService accessTokenService;

	private static final Logger log = LoggerFactory
			.getLogger(UidService.class);
	public String getUidByOpenId(String openId, String wxId, String appId, String appSecret){
		String result = "";
		AccessToken accessToken = null;
		try {
			accessToken = accessTokenService.getAccessToken(wxId, appId, appSecret);
			String url = String.format(Constants.GET_UID_URL,accessToken.getAccesstoken(),openId);
			String retStr = HttpClientUtils.get(url);
			if(StringUtils.isNotEmpty(retStr)){
				JSONObject retJson = JSONObject.parseObject(retStr);
				if(retJson.containsKey("unionid")){
					result = retJson.getString("unionid");
				}
			}
		} catch (Exception e) {
			log.error("get uid faild, the openId is:"+ openId,e);
		}
		return result;
	}

}
