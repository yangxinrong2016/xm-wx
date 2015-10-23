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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("customMsgService")
public class CustomMsgService {

	@Resource
	AccessTokenService accessTokenService;
	@Resource
	ICustomMsgServcie customServiceImpl;
	private static final Logger log = Logger.getLogger(CustomMsgService.class);

	public static final long MAX_RETRY = 3;

	public static final int ERROR_CODE_WAIT_SEND = -9991;
	public static final int ERROR_CODE_SEND_TO_WX_FAIL = -9992;

	public static final Map<Integer, String> ERROR_MSG_MAP = new HashMap<Integer, String>();
	static {
		ERROR_MSG_MAP.put(ERROR_CODE_WAIT_SEND, "等待发送给微信");
		ERROR_MSG_MAP.put(ERROR_CODE_SEND_TO_WX_FAIL, "发送给微信失败");
	}

	public static String getErrorCodeMsg(int errorCode) {
		String msg = ERROR_MSG_MAP.get(errorCode);
		return msg == null ? "未知错误码" : msg;
	}

	public void sendText(final String wxId, final String openid,
			final String content, final boolean accessTokenExpiredRetry) {
		long start = System.currentTimeMillis();
		try {
			String url = Constants.CUSTOM_MSGSEND_URL;
			AccessToken accessToken = accessTokenService.getAccessToken(wxId);
			// 文本客服消息

			JSONObject textContentJson = new JSONObject();
			textContentJson.put("content", content);
			JSONObject textJson = new JSONObject();
			textJson.put("touser", openid);
			textJson.put("msgtype", "text");
			textJson.put("text", textContentJson);
			String textContent = textJson.toString();
			log.debug("【文本】客服消息内容:{"+textContent+"}");

			// 先将要发送的模板消息入库
			Date now = new Date();
			CustomMsg ctsMsg = new CustomMsg();
			ctsMsg.setWxid(wxId);
			ctsMsg.setOpenid(openid);
			ctsMsg.setMsg(content);
			ctsMsg.setMsghash(Md5Util.md5AsLowerHex(content));
			ctsMsg.setMsgtype("text");
			ctsMsg.setErrorcode(ERROR_CODE_WAIT_SEND);
			ctsMsg.setCodemsg(getErrorCodeMsg(ERROR_CODE_WAIT_SEND));
			ctsMsg.setErrmsg("");
			ctsMsg.setCreatetime(now);
			ctsMsg.setUpdatetime(now);
			ctsMsg.setRetry(0);
			customServiceImpl.insert(ctsMsg);
			Integer ctsMsgId = ctsMsg.getId();
			url = String.format(url, accessToken.getAccesstoken());
			String retStr = HttpClientUtils.post(url, textContent, Constants.CHARSET_UTF8);
			log.debug("微信接口:{"+url+"}, 返回值:{"+retStr+"}");
			if (StringUtils.isEmpty(retStr)) {
				// 发送给微信失败
				ctsMsg = customServiceImpl.getById(ctsMsgId);
				ctsMsg.setErrorcode(ERROR_CODE_SEND_TO_WX_FAIL);
				ctsMsg.setErrmsg(getErrorCodeMsg(ERROR_CODE_SEND_TO_WX_FAIL));
				customServiceImpl.update(ctsMsg);
			}
			JSONObject retJson = JSONObject.parseObject(retStr);
			int errcode = retJson.get("errcode") == null ? -999999 : retJson.getIntValue("errcode");
			String errmsg = retJson.getString("errmsg");
			ctsMsg = customServiceImpl.getById(ctsMsgId);
			if(null == ctsMsg){
				ctsMsg = new CustomMsg();
			}
			ctsMsg.setErrorcode(errcode);
			ctsMsg.setCodemsg(WXErrorConstants.getErrorMsg(errcode));
			ctsMsg.setErrmsg(errmsg);
			customServiceImpl.update(ctsMsg);

			if (errcode != 0) {
				// 发送模板失败
				if (accessTokenExpiredRetry
						&& (errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_EXPIRED
						|| errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_ERROR || errcode == WXErrorConstants.ERROR_NO_ACCESS_TOKEN_INVALID)) {
					// access token过期或者非法或者错误，强制刷新，尝试一下
					accessTokenService.refreshAccessToken(wxId);
					sendText(wxId, openid, content, false);
					return;
				}
				log.error("调用【文本】发送客服消息接口失败, "
						+ WXErrorConstants.getErrorMsg(errcode));
				throw new WXException("调用【文本】发送客服消息接口失败, " + retStr);
			}
		}
//		catch (IOException e) {
//			log.error("微信返回数据解析异常, 接口:{}, 返回码:{}, 返回值:{}", api, httpCode,
//					response);
//			e.printStackTrace();
//			AutoLogs.log(AutoLogs.LOG_TYPE_IOEXCEPTION,
//					(int) (System.currentTimeMillis() - start));
//		} catch (SQLException e) {
//			log.error("数据库异常，" + e.getMessage());
//			e.printStackTrace();
//			AutoLogs.log(AutoLogs.LOG_TYPE_SQL_EXCEPTION,
//					(int) (System.currentTimeMillis() - start));
//		} catch (WXException e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//			AutoLogs.log(AutoLogs.LOG_TYPE_WXEXCEPTION_CUSTOMMSG,
//					(int) (System.currentTimeMillis() - start));
//		}
		catch (Exception e) {
			log.error("send custom failed", e);

		}
	}
}
