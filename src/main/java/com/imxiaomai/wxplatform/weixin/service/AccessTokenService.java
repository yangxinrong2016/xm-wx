package com.imxiaomai.wxplatform.weixin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.AccessToken;
import com.imxiaomai.wxplatform.service.IAccessTokenService;
import com.imxiaomai.wxplatform.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Service("accessTokenService")
public class AccessTokenService {
    
    private static final Logger log = LoggerFactory.getLogger(AccessTokenService.class);
    @Resource
    private IAccessTokenService accessTokenServiceImpl;
    @Value("${app.id}")
    public String APP_ID;
    @Value("${app.secret}")
    public String APP_SECRET;


    public AccessToken getAccessToken(String wxId) throws Exception {
        return getAccessToken(wxId, APP_ID, APP_SECRET);
    }
    /**
     * 得到accessToken
     * 注意：此函数不保证安全的，需要一个脚本进行定期刷新
     * @param wxId
     * @return null出错了，其他正常
     */
    public AccessToken getAccessToken(String wxId, String appId, String appSecret) throws Exception {
        AccessToken accessToken = null;
        try {
            accessToken = accessTokenServiceImpl.getAccessTokenByWxId(wxId);
            if(accessToken == null) {
                //还没有此token，获取一下
                accessToken = refreshAccessToken(wxId, appId, appSecret);
            }else {
                long now = new Date().getTime();
                //如果接近过期时间10分钟内，就进行刷新，一般是由后台脚本进行集中刷新，这里只是进行一次保险
                if(accessToken.getExpiretime().getTime() - 10*60*1000 <= now) {
                    accessToken = refreshAccessToken(wxId);
                }
            }
        } catch (Exception e) {
            log.error("获取AccessToken失败，",e);
        }

        if(null == accessToken) {
            throw new Exception();
        }

        return accessToken;
        
    }

    public AccessToken refreshAccessToken(String wxId){
        return refreshAccessToken(wxId, APP_ID, APP_SECRET);
    }
    
    /**
     * 刷新access token
     * 
     * @param wxId
     * @return 失败，null；成功
     */
    public AccessToken refreshAccessToken(String wxId,String appId, String appSecret) {
        AccessToken retAccessToken = null;
        try {
            String url = String.format(Constants.GET_ACCESSTOKEN_URL, appId, appSecret);
            String retStr = HttpClientUtils.get(url);
            JSONObject ret = JSONObject.parseObject(retStr);
            String errcode = ret.getString("errcode");
            String token = ret.get("access_token") == null ? null : ret.get("access_token").toString();
            int expires = ret.get("expires_in") == null ? 0 : Integer.valueOf(ret.get("expires_in").toString());
            if(null != errcode){
                //获取accesstoken失败
                log.error("获取微信accesstoken失败,"+retStr);
                return null;
            }else {
                AccessToken accessToken = accessTokenServiceImpl.getAccessTokenByWxId(wxId);
                long nowLong = new Date().getTime();
                Date now = new Date();
                if(accessToken == null){
                    //原来还没有，这个逻辑最好是在脚本中进行统一处理，这里做一次保险处理
                    AccessToken newToken = new AccessToken();
                    newToken.setAccesstoken(token);
                    newToken.setCreatetime(now);
                    newToken.setUpdatetime(now);
                    newToken.setWxid(wxId);
                    newToken.setExpiretime(new Date(nowLong + 1000 * expires));
                    accessTokenServiceImpl.insertAccessToken(newToken);
                    retAccessToken = newToken;
                    return retAccessToken;
                }else {
                    //更新
                    accessToken.setExpiretime(new Date(nowLong + 1000*expires));
                    accessToken.setUpdatetime(now);
                    accessToken.setAccesstoken(token);
                    accessTokenServiceImpl.updateAccessToken(accessToken);
                    return accessToken;
                }
            }
        }catch (Exception e){
            log.error("reflash access token failed:" , e);
        }
        return retAccessToken;
    }
}
