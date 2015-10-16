package com.imxiaomai.wxplatform.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 *
 * @author yaowen
 *
 */
public class HttpClientUtils {
	private static Logger logger = Logger.getLogger(HttpClientUtils.class);
	private static final int HTTP_TIMEOUT = 500;
	private static final int HTTP_CON_TIMEOUT = 500;
	private static final int HTTP_CON_REQ_TIMEOUT = 500;
	private static final int HTTP_MAX_PERROUTE = 500;
	private static final int HTTP_MAX_TOTAL = 1000;
	private static final String DEF_CHARSET = "UTF-8";

	private static CloseableHttpClient httpClient = null;
	private static RequestConfig defaultRequestConfig = null;

	static {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		connManager.setDefaultSocketConfig(socketConfig);
		connManager.setMaxTotal(HTTP_MAX_TOTAL);
		connManager.setDefaultMaxPerRoute(HTTP_MAX_PERROUTE);
		defaultRequestConfig = RequestConfig.custom().setSocketTimeout(HTTP_TIMEOUT)	.setConnectTimeout(HTTP_CON_TIMEOUT).setConnectionRequestTimeout(HTTP_CON_REQ_TIMEOUT).build();
		httpClient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(defaultRequestConfig).build();
	}

	public static String get(String baseUrl, Map<String, Object> params, int timeout) throws Exception {
		return get(baseUrl, params, timeout, null);
	}

	public static String get(String baseUrl, Map<String, Object> params, int timeout, String encoding) throws Exception {
		String url = genUrl(params, baseUrl, encoding);
		return get(url, timeout);
	}

	public static String get(String url) throws Exception {
		return get(url, HTTP_TIMEOUT);
	}

	public static String get(HttpGet httpget, int timeout) throws Exception {
		try {
			long st = System.currentTimeMillis();
			if (timeout < 0) {
				return "";
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).build();
			httpget.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String rt = EntityUtils.toString(entity, DEF_CHARSET);
			EntityUtils.consume(entity);
			if (logger.isInfoEnabled()) {
				logger.info(httpget.getURI().toString() + " time used:" + (System.currentTimeMillis() - st));
			}
			return rt;
		} catch (Exception e) {
			logger.error("http error.url:" + httpget.getURI().toString(), e);
			throw e;
		}
	}

	public static String get(String url, int timeout) throws Exception {
		try {
			long st = System.currentTimeMillis();
			if (timeout < 0) {
				return "";
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(timeout).build();
			httpget.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();

			String rt = EntityUtils.toString(entity, DEF_CHARSET);
			EntityUtils.consume(entity);

			if (logger.isInfoEnabled()) {
				logger.info(url + " time used:" + (System.currentTimeMillis() - st));
			}
			return rt;
		} catch (Exception e) {
			logger.error("http error.url:" + url, e);
			throw e;
		}
	}

	public static String post(String url, String body, String encoding) throws Exception {
		try{
			HttpEntity entity = new StringEntity(body, encoding);
			return post(url, entity, HTTP_TIMEOUT, encoding);
		}catch (Exception e){
			logger.error("http error.url:" + url, e);
			throw e;
		}
	}


	public static String post(String url, Map<String, Object> params, String encoding) throws Exception{
		return post(url, params, HTTP_TIMEOUT, encoding);
	}
	public static String post(String url, Map<String, Object> params, int timeout, String encoding) throws Exception {
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Set<String> keySet = params.keySet();
			for(String key : keySet) {
				nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
			HttpEntity entity = new UrlEncodedFormEntity(nvps, encoding);
			return post(url, entity, timeout, encoding);
		} catch (Exception e) {
			logger.error("http error.url:" + url, e);
			throw e;
		}
	}

	public static String post(String url, HttpEntity entity, int timeout, String encoding) throws Exception{
		try {
			if(StringUtils.isEmpty(encoding)){
				encoding = DEF_CHARSET;
			}
			long st = System.currentTimeMillis();
			if (timeout < 0) {
				return "";
			}
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(timeout).build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String rt = EntityUtils.toString(responseEntity, encoding);
			EntityUtils.consume(responseEntity);
			if (logger.isInfoEnabled()) {
				logger.info(url + " time used:" + (System.currentTimeMillis() - st));
			}
			return rt;
		} catch (Exception e) {
			logger.error("http error.url:" + url, e);
			throw e;
		}
	}

	public static byte[] getBytes(String url, int timeout) throws Exception {
		try {
			long st = System.currentTimeMillis();
			if (timeout < 0) {
				return null;
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(timeout).build();
			httpget.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();

			byte[] rt = EntityUtils.toByteArray(entity);
			EntityUtils.consume(entity);

			if (logger.isInfoEnabled()) {
				logger.info(url + " time used:" + (System.currentTimeMillis() - st));
			}
			return rt;
		} catch (Exception e) {
			logger.error("http error.url:" + url, e);
			throw e;
		}
	}

	public static byte[] getBytes(String baseUrl, Map<String, Object> params, int timeout, String encoding)
			throws Exception {
		String url = genUrl(params, baseUrl, encoding);
		return getBytes(url, timeout);
	}

	public static String encode(String str, String encode) {
		if (encode == null) {
			return str;
		}
		try {
			return URLEncoder.encode(str, encode);
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	public static int getTimeout(String timeout) {
		if (StringUtils.isEmpty(timeout)) {
			return HTTP_TIMEOUT;
		}
		try {
			return Integer.valueOf(timeout);
		} catch (Exception e) {
			logger.error("fail to parse timeout ", e);
			return HTTP_TIMEOUT;
		}
	}

	public static String genUrl(Map<String, Object> params, String baseUrl, final String encoding) {
		StringBuilder url = new StringBuilder(baseUrl);
		url.append("?");
		url.append(Joiner.on("&").join(
				Maps.transformEntries(params, new Maps.EntryTransformer<String, Object, String>() {
					@Override
					public String transformEntry(String key, Object value) {
						if (key == null) {
							return "";
						}
						if (value == null) {
							return key + "=";
						}
						return key + "=" + encode(value.toString(), encoding);
					}

				}).values()));
		return url.toString();
	}

	private static Map<String, String> splitUrlParams(String urlParams, String decodeCoding) {
		Map<String, String> urlParamsMap = new HashMap<String, String>();
		if (urlParams != null) {
			for (String str : Splitter.on('&').split(urlParams)) {
				String[] kv = str.split("=");
				if (kv != null && kv.length == 2) {
					if (StringUtils.isNotBlank(decodeCoding)) {
						try {
							urlParamsMap.put(kv[0], URLDecoder.decode(kv[1], decodeCoding));
						} catch (UnsupportedEncodingException e) {
							logger.error("", e);
						}
					} else {
						urlParamsMap.put(kv[0], kv[1]);
					}
				}
			}
		}
		return urlParamsMap;
	}

	public static Map<String, String> splitUrlParams(String urlParams) {
		return splitUrlParams(urlParams, null);
	}

	public static String genUrl(Map<String, Object> params, String baseUrl) {
		return genUrl(params, baseUrl, null);
	}


}
