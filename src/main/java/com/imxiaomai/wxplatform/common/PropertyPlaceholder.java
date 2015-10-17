package com.imxiaomai.wxplatform.common;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 *
 * @author yaowen
 *
 */
public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {
	private static Properties properties = new Properties();

	@Override
	protected Properties mergeProperties() throws IOException {
		Properties props =  super.mergeProperties();
		return props;
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
}
