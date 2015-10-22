package com.imxiaomai.wxplatform.weixin.service;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.common.WXErrorConstants;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.domain.Menu;
import com.imxiaomai.wxplatform.service.IMenuService;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zengyaowen on 15-10-14.
 */
@Service("weixinMenuService")
public class WeixinMenuService {

    @Resource
    private IMenuService menuServiceImpl;
    @Resource
    private AccessTokenService accessTokenService;
    private static final Logger log = Logger.getLogger(WeixinMenuService.class);
    public void createMenu(String wxId, boolean accessTokenExpiredRetry) throws Exception {
        int httpCode = 0;
        Menu menu = menuServiceImpl.getMenuByWxId(wxId);
        if(null == menu){
            throw new Exception("menu content is empty");
        }
        String menuContent = menu.getContent();
        if (log.isDebugEnabled()) {
            log.debug("自定义菜单:{}" + menuContent);
        }
        AccessToken accessToken = accessTokenService.getAccessToken(wxId);
        String url = Constants.CREATE_MENU_URL + accessToken.getAccesstoken();
        String ret = HttpClientUtils.post(url, menuContent, Constants.CHARSET_UTF8);
        JSONObject retObject = JSONObject.parseObject(ret);
        httpCode = retObject.getIntValue("errcode");
        if (log.isDebugEnabled()) {
            log.debug("微信接口:{"+url+"}, 返回码:{"+httpCode+"}, 返回值:{"+retObject.get("errmsg")+"}");
        }
        if (httpCode != 0) {
            //创建菜单失败
            if (accessTokenExpiredRetry &&
                    (httpCode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_EXPIRED ||
                            httpCode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_ERROR ||
                            httpCode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_INVALID)) {
                //access token过期或者非法或者错误，强制刷新，尝试一下
                accessTokenService.refreshAccessToken(wxId);
                createMenu(wxId, false);
                return;
            }
            log.error("调用创建自定义菜单接口失败");
        }
    }
}

