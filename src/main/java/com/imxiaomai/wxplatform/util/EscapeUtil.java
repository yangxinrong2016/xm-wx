package com.imxiaomai.wxplatform.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class EscapeUtil {

	public static final String UTF8 = "UTF-8";
	public static final String UTF16LE = "UTF-16LE";
	public static final String GBK = "GB18030";
	public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);
	public static final Charset CHARSET_UTF16LE = Charset.forName(UTF16LE);
	public static final Charset CHARSET_GBK = Charset.forName(GBK);

	public static String encGbk(String param) {
		return enc(param, GBK);
	}
	public static String encUtf8(String param) {
		return enc(param, UTF8);
	}
	private static String enc(String param, String encoding) {
		try {
			if (param == null)
				return null;
			return URLEncoder.encode(param, encoding);
		} catch (Exception e) {
			logger.error("should not error. (encode):" + param, e);
			return param;
		}
	}

	/** XML中，有5个字符需要转义 */
	public static String escapeXml(Object val) {
		if (val == null)
			return "";
		//对null的处理主要是考虑，此函数用在拼xml时，返回null拼字符串时会变成"null"
		return StringEscapeUtils.escapeXml(String.valueOf(val));
	}

	public static String escapeHtml(Object val) {
		return _htmlTranslator.translate(String.valueOf(val));
	}
	//ie6/7中不识别&apos;，所以不能像xml一样转义5个字符，只能转义4个字符
	private static final CharSequenceTranslator _htmlTranslator = new LookupTranslator(new String[][] { //
			{ "<", "&lt;" }, // < - less-than
			{ ">", "&gt;" }, // > - greater-than
			{ "&", "&amp;" }, // & - ampersand
			{ "\"", "&#39;" }, // " - double-quote
			{ "'", "&#34;" }, // XML apostrophe
	});

	static final Logger logger = LoggerFactory.getLogger(EscapeUtil.class);

}