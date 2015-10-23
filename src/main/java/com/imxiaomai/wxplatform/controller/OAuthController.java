package com.imxiaomai.wxplatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.WxUser;
import com.imxiaomai.wxplatform.util.EscapeUtil;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import com.imxiaomai.wxplatform.weixin.service.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private static final Logger log = Logger.getLogger(OAuthController.class);
    @Resource
    OAuthService oAuthService;
    public static final Pattern urlPattern = Pattern.compile("url:([^,]+)[,}]");
    public static final Pattern typePattern = Pattern.compile("type:([^,]+)[,}]");
    public static final Pattern pPattern = Pattern.compile("p:([^,]+)[,}]");

    @RequestMapping("/redirect")
    public void redirect(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam String state, @RequestParam(required = false, defaultValue = "") String code) throws IOException {

        long start = System.currentTimeMillis();
        if(log.isDebugEnabled()){
            log.debug("/oauth/redirect state:" + state);
        }
        if (StringUtils.isEmpty(code)) {
            if(log.isDebugEnabled()){
                log.debug("用户拒绝授权");
            }
            gotoErrorPage(request, response);
        }
        String type = null;
        String url = null;
        String args = null;
        //这里很关键，线上数据已经表明，微信的自定义跳转，会有小部分（大概占到10%）请求中的双引号被微信过滤掉
        //这样会导致部分请求出错，这里进行过滤
        try {
            JSONObject stateJsonNode = JSONObject.parseObject(state);
            type = stateJsonNode.getString("type") == null ? null : stateJsonNode.getString("type");
            url = stateJsonNode.getString("url") == null ? null : stateJsonNode.getString("url");
            args = stateJsonNode.getString("p") == null ? null : stateJsonNode.getString("p");
        } catch (Exception e) {
            //TODO将正则表达式替换掉，提升性能
            log.error("解析state的json数据出错，这里进行容错处理:" ,e);
            Matcher matcher = urlPattern.matcher(state);
            if (matcher.find()) {
                //符合
                String g1 = matcher.group(1);
                if (g1 != null) {
                    url = g1;
                }
            }
            matcher = typePattern.matcher(state);
            if (matcher.find()) {
                //符合
                String g1 = matcher.group(1);
                if (g1 != null) {
                    type = g1;
                }
            }
            matcher = pPattern.matcher(state);
            if (matcher.find()) {
                //符合
                String g1 = matcher.group(1);
                if (g1 != null) {
                    args = g1;
                }
            }
        }
        String openid = "";
        try {
            if(log.isDebugEnabled()){
                log.debug("h5mall/foward/grap url {"+url+"}");
            }
            //Hack 由于回调h5mall/foward/grap时需要传递packetCode&accessToken，但微信state参数最多支持128字节。所以url参数采用简写做map映射！
            if (StringUtils.isEmpty(url) && Constants.getUrl(url) != null) {
                url = Constants.getUrl(url) + args;
                if(log.isDebugEnabled()){
                    log.debug(url+"h5mall/foward/grap {"+args+"} type {" + type+ "}");
                }
            }
            //url信息
            if (url == null) {
                log.error("url为空, state:" + state);
                gotoErrorPage(request, response);
            }
            URL urlURL = new URL(url);
            String query = urlURL.getQuery();
            if (type == null || type.equals("")) {
                log.debug("参数错误，state中的type参数为空");
                gotoErrorPage(request, response, url);
            } else if (type.equals(Constants.OAUTH_TYPE_BASE)) {
                //只需要获得用户的openid信息
                openid = oAuthService.getOauthAccessTokenDTO(code).getOpenId();
                log.debug("微信用户openid:" + openid);
                /*
                 * 安全角度考虑，链接中不再携带openid
                String tmp = "openid="+openid;

                if(query == null || query.equals("")){
                    query = tmp;
                }else{
                    query += "&" + tmp;
                }
                */
            } else if (type.equals("lx")) {
                openid = oAuthService.getOauthAccessTokenDTO(code).getOpenId();
                query = (StringUtils.isBlank(query) ? "" : query + "&") + "openid=" + openid;
                url = String.format("%s://%s%s%s?%s", urlURL.getProtocol(), urlURL.getHost(), urlURL.getPath(), urlURL.getRef() != null ? "#" + urlURL.getRef() : "", query);
                response.sendRedirect(url);
            } else if (type.equals(Constants.OAUTH_TYPE_USER_INFO)) {
                //需要获得用户的基本信息，会弹出绿色的用户授权页面（是微信自身的页面）
                WxUser wxUser = oAuthService.getWxUser(code);
                openid = wxUser.getOpenid();
                //log.debug("微信用户信息:" + gson.toJson(wxUser));
                if (StringUtils.isEmpty(query)) {
                    query = getUrlParameterString(wxUser);
                } else {
                    query += "&" + getUrlParameterString(wxUser);
                }
            } else {
                log.debug("参数错误，state中的type参数未知，不知道怎么处理。type:" + type);
                gotoErrorPage(request, response, url);
            }
            url = String.format("%s://%s%s?%s", urlURL.getProtocol(), urlURL.getHost(), urlURL.getPath(), query);
            log.debug("oauth跳转到:" + url);
        } catch (WXException e) {
            log.error("获得微信用户信息失败:" , e);
//            AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_OAUTH, (int) (System.currentTimeMillis() - start));
            gotoErrorPage(request, response, url);
        } catch (Exception e) {
            log.error("获得微信用户信息失败:" , e);
            gotoErrorPage(request, response, url);
        }
        //埋openid的cookie
        if(StringUtils.isNotEmpty(openid)){
            Cookie openIdCookie = new Cookie(Constants.COOKIE_NAME_OPEN_ID, openid);
            response.addCookie(openIdCookie);
            openIdCookie.setDomain(".imxiaomai.com");
            openIdCookie.setPath("/");
            openIdCookie.setMaxAge(864000);
            response.sendRedirect(url);
        } else{
            gotoErrorPage(request, response, url);
        }
    }
    public void gotoErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        gotoErrorPage(request, response, "");
    }

    public void gotoErrorPage(HttpServletRequest request, HttpServletResponse response,String url) throws IOException {
        if(log.isDebugEnabled()){
            log.error("go to error page, url: " + url);
        }
        if(StringUtils.isEmpty(url)){
            url = "";
        }
        response.sendRedirect(url);
    }

    public String getUrlParameterString(WxUser wxUser) {
        //不生成openid
        return String.format("nickname=%s&sex=%s&province=%s&city=%s&country=%s&headimgurl=%s&unionid=%s",
                EscapeUtil.encUtf8(wxUser.getNickname()), EscapeUtil.encUtf8(wxUser.getSex()), EscapeUtil.encUtf8(wxUser.getProvince()), EscapeUtil.encUtf8(wxUser.getCity()), EscapeUtil.encUtf8(wxUser.getCountry()), EscapeUtil.encUtf8(wxUser.getHeadimgurl()), EscapeUtil.encUtf8(wxUser.getUnionid()));
    }
}
