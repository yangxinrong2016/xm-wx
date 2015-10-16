package com.imxiaomai.wxplatform.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

/**
 * escape html for request
 * 
 * @author yaowen
 *
 *
 */
public class HtmlEscapeFilter implements Filter {
	public HtmlEscapeFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		chain.doFilter(new EscapeHtmlRequestWraper((HttpServletRequest) request), response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}

class EscapeHtmlRequestWraper extends HttpServletRequestWrapper {
	final static String[] TO_REPLACE_CHARS = new String[] { "<", ">", "'", "\"" };
	private Map<String, String[]> parameters;

	@SuppressWarnings("unchecked")
	public EscapeHtmlRequestWraper(HttpServletRequest request) {
		super(request);
		parameters = doEscapeHtml((Map<String, String[]>) request.getParameterMap());

	}

	@Override
	public String getParameter(String name) {
		String[] value = parameters.get(name);
		if (value == null || value.length == 0) {
			return null;
		}
		return value[0];
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getParameterMap() {
		return parameters;
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameters.get(name);
	}

	private Map<String, String[]> doEscapeHtml(Map<String, String[]> parameterMap) {
		Map<String, String[]> result = new HashMap<String, String[]>();
		if (parameterMap == null) {
			return result;
		}
		Set<String> keys = parameterMap.keySet();
		for (String key : keys) {
			String[] value = parameterMap.get(key);
			String[] finalValue = null;
			if (value != null) {
				finalValue = escapeHtml(value);
			}
			result.put(key, finalValue);
		}

		return result;
	}

	private String[] escapeHtml(String[] strs) {
		if (strs == null) {
			return null;
		}
		String[] result = new String[strs.length];
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] != null) {
				// result[i] = StringEscapeUtils.escapeHtml(strs[i]);
				result[i] = replace(strs[i], TO_REPLACE_CHARS);
			}
		}
		return result;
	}

	private String replace(String text, String[] toReplace) {
		String value = text;
		for (String r : toReplace) {
			value = StringUtils.replace(value, r, "");
		}
		return value;
	}

}