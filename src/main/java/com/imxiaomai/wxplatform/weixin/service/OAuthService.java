package com.imxiaomai.wxplatform.weixin.service;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.WxUser;
import com.imxiaomai.wxplatform.dto.OauthAccessTokenDTO;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("oAuthService")
public class OAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);
    @Value("${app.id}")
    public String APP_ID;
    @Value("${app.secret}")
    public String APP_SECRET;
    @Value("${wxid}")
    public String WX_ID;
    /**
     * 得到openid
     * @param code
     * @return null出错了，其他正常
     * @throws WXException 
     */
    public OauthAccessTokenDTO getOauthAccessTokenDTO(String code) throws WXException {
        String api = null;
        int httpCode = 0;
        String response = null;
        OauthAccessTokenDTO oauthAccessTokenDTO = new OauthAccessTokenDTO();
        try {
            String url= Constants.OAUTH_ACCESS_TOKEN_URL;
            url = String.format(url, APP_ID, APP_SECRET, code);
            String retStr = HttpClientUtils.get(url);
            if(StringUtils.isEmpty(retStr)){
                return oauthAccessTokenDTO;
            }
            JSONObject responseJsonNode = JSONObject.parseObject(retStr);
            String errcode = responseJsonNode.getString("errcode");
            String openid = responseJsonNode.getString("openid");
            String token = responseJsonNode.getString("access_token");
            if(errcode != null){
                //获取accesstoken失败
                log.error("获取微信openId失败,"+response);
                throw new WXException("获取微信openId失败,"+response);
            }else {
                oauthAccessTokenDTO.setOpenId(openid);
                oauthAccessTokenDTO.setToken(token);
            }
            
        }catch (Exception e) {
            log.error("oauth service getOpenId failed", e);
        }
        return oauthAccessTokenDTO;
    }
    
    
    /**
     * 得到wx用户信息（包括openId和用户昵称，省市，头像等数据）
     * @param code
     * @return null出错了，其他正常
     * @throws WXException 
     */
    public WxUser getWxUser(String code) throws Exception {
        OauthAccessTokenDTO oauthAccessTokenDTO = getOauthAccessTokenDTO(code);
        String url = Constants.OAUTH_GET_USER_URL;
        String token = oauthAccessTokenDTO.getToken();
        String openid = oauthAccessTokenDTO.getOpenId();
        url = String.format(url, token, openid);
        String retStr = HttpClientUtils.get(url);
        if(StringUtils.isEmpty(retStr)){
           log.error("get oauthAccessToken failed");
        }
        JSONObject responseJsonNode = JSONObject.parseObject(retStr);
        String errcode = responseJsonNode.getString("errcode");
        String errmsg = responseJsonNode.getString("errmsg");
        if(errcode != null){
            //获取accesstoken失败
            log.error("获取微信用户信息失败,"+retStr);
            throw new WXException("获取微信用户信息失败,"+retStr);
        }
        WxUser wxUser = new WxUser();
        wxUser.setWxid(WX_ID);
        wxUser.setOpenid(openid);
        wxUser.setNickname(responseJsonNode.getString("nickname"));
        wxUser.setSex(responseJsonNode.getString("sex"));
        wxUser.setProvince(responseJsonNode.getString("province"));
        wxUser.setCity(responseJsonNode.getString("city"));
        wxUser.setHeadimgurl(responseJsonNode.getString("headimgurl"));
        wxUser.setUnionid(responseJsonNode.getString("unionid"));
        wxUser.setCountry(responseJsonNode.getString("country"));
        return wxUser;
    }
}
