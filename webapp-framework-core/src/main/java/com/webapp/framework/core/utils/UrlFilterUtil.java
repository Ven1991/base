package com.webapp.framework.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import com.webapp.framework.core.beans.CanAccessActionBean;
import com.webapp.framework.core.beans.UrlFilters;
import com.webapp.framework.core.cache.oscache.OsCache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UrlFilterUtil {
	private static Logger log = LogManager.getLogger(UrlFilterUtil.class);
	private static final String UrlFilterTools_KEY = "UrlFilterTools_KEY";

	public static void loadUrlFilter() {
		ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();
		String location = "classpath*:urlFilter/*-FilterMap.xml";
		try {
			Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);

			if (null == resources) {
				return;
			}
			for (Resource resource : resources) {
				log.info("加载过滤清单[" + resource + "]...");
				try {
					byte[] buf = loadFileData(new EncodedResource(resource));
					String xml = new String(buf, "UTF-8");
					loadUrlFilter(xml);
				} catch (Exception e) {
					log.error("加载过滤清单失败[" + resource + "]：" + e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error("加载过滤清单失败：" + e.getMessage());
		}
	}

	protected static byte[] loadFileData(EncodedResource encodedResource) throws IOException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		try {
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				byte[] buf;
				while (true) {
					buf = new byte[8192];
					int i = inputStream.read(buf);
					if (i <= 0)
						break;
					bs.write(buf, 0, i);
				}

				return bs.toByteArray();
			} finally {
				inputStream.close();
			}
		} catch (IOException ex) {
			throw ex;
		}
	}

	private static void loadUrlFilter(String xml) throws JAXBException {
		JaxbMapper jm = new JaxbMapper(new Class[] { UrlFilters.class });
		try {
			UrlFilters bean = (UrlFilters) jm.fromXml(xml);
			if (null == bean.getUrlFilters()) {
				return;
			}
			Map map = (Map) OsCache.getInstance().get("UrlFilterTools_KEY");
			if (null == map) {
				map = new HashMap();
			}

			for (CanAccessActionBean item : bean.getUrlFilters()) {
				String key = item.getNamespace();
				if (StringUtil.isNotNull(item.getMethod())) {
					key = key + "/" + item.getMethod();
				}
				if (!map.containsKey(key)) {
					map.put(key, item);
				}
			}
//			OsCache.getInstance().put("UrlFilterTools_KEY", map);
		} catch (Exception e) {
			log.error("装载XML失败，可能这个过滤文件配置不正确：" + e.getMessage());
		}
	}

	public static boolean isNeedCheckAuth(String namespace, String method, boolean isUserLogin) {
		Map map = (Map) OsCache.getInstance().get("UrlFilterTools_KEY");
		if (null == map) {
			return true;
		}

		CanAccessActionBean bean = (CanAccessActionBean) map.get(namespace + "/" + method);
		if (null != bean) {
			return isNeedCheckAuth(bean, isUserLogin, method);
		}

		bean = (CanAccessActionBean) map.get(namespace);
		if (null != bean) {
			return isNeedCheckAuth(bean, isUserLogin, method);
		}
		return true;
	}

	private static boolean isNeedCheckAuth(CanAccessActionBean bean, boolean isUserLogin, String method) {
		if ((bean.isNeedUserLogin()) && (!isUserLogin))
			return true;
		return false;
	}
}