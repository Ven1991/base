package com.webapp.framework.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class PropertiesLoader {
	private static Logger logger = LogManager.getLogger(PropertiesLoader.class);

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();
	private Properties properties;

	public PropertiesLoader(String[] resourcesPaths) {
		this.properties = loadProperties(resourcesPaths);
	}

	public Properties getProperties() {
		return this.properties;
	}

	public String getProperty(String key) {
		String result = System.getProperty(key);
		if (result != null) {
			return result;
		}
		return this.properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		String result = getProperty(key);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}

	public Integer getInteger(String key) {
		return Integer.valueOf(getProperty(key));
	}

	public Integer getInteger(String key, Integer defaultValue) {
		return Integer.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public Boolean getBoolean(String key) {
		return Boolean.valueOf(getProperty(key));
	}

	public Boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	private Properties loadProperties(String[] resourcesPaths) {
		Properties props = new Properties();
		for (String location : resourcesPaths) {
			logger.debug("Loading properties file:{}", location);
			InputStream is = null;
			try {
				Resource resource = resourceLoader.getResource(location);
				if (!resource.exists()) {
					resource = new FileSystemResourceLoader().getResource(location);
				}
				if (!resource.exists()) {
					logger.warn("Could not load properties resource=[{}] is not exists", resource);

					IOUtils.closeQuietly(is);
				} else {
					is = resource.getInputStream();
					String encoding = FileUtil.getFileCharacterEnding(resource.getFile());
					props.load(new BufferedReader(new InputStreamReader(is, encoding)));
				}
			} catch (IOException ex) {
				logger.error("Could not load properties from path=[{}],exception:{}", location, ex);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return props;
	}
}