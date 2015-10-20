package com.imxiaomai.wxplatform.common;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by zengyaowen on 15-10-13.
 */
public class Constants {
        /*
        线上微信的appid,app_secret,wxid.
         */
//        public static final String APP_ID = "wx35a8b3f8507f4ea6";
//        public static final String APP_SECRET = "c720c0622d57500de1e742b87efd5f2c";
//        public static final String WX_ID = "gh_6f80d5b4122f";
//        public static final String TOKEN = "token";

        /*
        测试环境“迈克思”
         */
        //        public static final String APP_ID="wxd888b8a9c5763733";
        //        public static final String APP_SECRET="ff3ec3c95ed8f323a5202caa5ed2138a";
        //        public static final String WX_ID="gh_6fa204cb56a1";
        //        public static final String TOKEN="imxiaomai123";
        public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
        public static final String GET_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        public static final String CHARSET_UTF8 = "UTF-8";
        public static final int SUCCESS_CODE = 0;
        public static final int ERROR_CODE = -1;
        public static final String DEFAULT_RESPONSE_CONTENT = "默认回复";
        private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("url", Locale.CHINA);
        public static final String OAUTH_TYPE_BASE = "base";
        public static final String OAUTH_TYPE_USER_INFO = "user_info";
        public static final String SEND_TPL_MSG_TOKEN = "wodk123lkokowkdk1231kdockfjokd";
        public static final int WX_USER_IS_SUBSCRIBE = 1;
        public static final int WX_USER_NOT_SUBSCRIBE = 0;
        public static final int WX_USER_SOURCE_COMMON = 0;
        public static final String WX_RESPONSE_TYPE_TEXT = "text";
        public static String getBindUrl() {
                return RESOURCE_BUNDLE.getString("bind");
        }
//        public static final String P2P_ACTIVE_URL = "http://127.0.0.1:8092/p2p/wx/active";
//        public static final String P2P_GETMSG_URL = "http://127.0.0.1:8092/p2p/wx/menuNotify?openid=%s";
        public static final String CUSTOM_MSGSEND_URL= "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
        public static final String TEMPLATE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        public static final String OAUTH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        public static final String OAUTH_GET_USER_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
        public static final String GET_UID__URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
        public static final String COOKIE_NAME_OPEN_ID = "xiaomai_open_id";
        public static final String WEI_XIN_FOWARD_GRAP = "fg";
//        private static final Config defH5MallURL = Config.of("h5mall_url", "http://m.m.mall.test.imxiaomai.com");
        public static final Map<String, String> urlMap = new HashMap<String, String>();
        static {
                urlMap.put(WEI_XIN_FOWARD_GRAP, "http://m.m.mall.test.imxiaomai.com/forward/grap");
        }
        public static String getUrl(String ab) {
                return urlMap.get(ab);
        }
}
