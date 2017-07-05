package com.webapp.framework.base.common.utils.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.webapp.framework.core.exception.BaseException;
import com.webapp.framework.core.utils.StringUtil;

public class HttpPost {
	public static byte[] doPost(String url) {
		Map map = new HashMap();
		return doPost(url, map);
	}

	public static byte[] doPost(String url, Map<String, String> map) {
		return doPost(url, "UTF-8", map);
	}

	public static byte[] doPost(String url, String encode, Map<String, String> map) {
		HttpClient client = new HttpClient();
		client.getParams().setSoTimeout(60000);
		PostMethod method = new PostMethod(url);

		method.getParams().setContentCharset(encode);
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			String key = (String) localIterator.next();
			String value = (String) map.get(key);
			if (!StringUtil.isNull(value)) {
				method.addParameter(key, value);
			}
		}
		try {
			String key;
			int status = client.executeMethod(method);
			if (status != 200) {
				throw new Exception(String.format("服务器返回错误：[%d][%s]", new Object[] { Integer.valueOf(status), url }));
			}
			return method.getResponseBody();
		} catch (Exception ex) {
			throw new BaseException(String.format("发送数据失败：[%s][%s]", new Object[] { ex.getMessage(), url }), ex);
		} finally {
			method.releaseConnection();
		}
	}

	public static byte[] doPost(String url, byte[] buf) {
		return doPost(url, buf, "UTF-8");
	}

	public static byte[] doPost(String url, byte[] buf, String enCode) {
		HttpClient client = new HttpClient();
		client.getParams().setSoTimeout(60000);
		PostMethod method = new PostMethod(url);

		method.setRequestHeader("Content-Type", "application/xml; charset=" + enCode);
		method.setRequestEntity(new ByteArrayRequestEntity(buf));
		try {
			int status = client.executeMethod(method);
			if (status != 200) {
				throw new Exception(String.format("服务器返回错误：[%d][%s]", new Object[] { Integer.valueOf(status), url }));
			}
			return method.getResponseBody();
		} catch (Exception ex) {
			throw new BaseException(String.format("发送数据失败：[%s][%s]", new Object[] { ex.getMessage(), url }), ex);
		} finally {
			method.releaseConnection();
		}
	}

	public static byte[] doGet(String url) {
		return doGet(url, "UTF-8");
	}

	public static byte[] doGet(String url, String enCode) {
		Map map = new HashMap();
		return doGet(url, map, enCode);
	}

	public static byte[] doGet(String url, Map<String, String> map, String enCode) {
		HttpClient client = new HttpClient();
		client.getParams().setSoTimeout(60000);
		String Params = "";
		if ((map != null) && (!map.isEmpty())) {
			Params = "&";
			if (url.indexOf(63) < 0) {
				Params = "?";
			}
			for (String key : map.keySet()) {
				String value = (String) map.get(key);
				if (!StringUtil.isNull(value)) {
					Params = Params + key + "=" + value;
				}
			}
		}
		// String value;
		GetMethod method = new GetMethod(url + Params);

		method.setRequestHeader("Content-Type", "application/xml; charset=" + enCode);
		try {
			int status = client.executeMethod(method);
			if (status != 200) {
				throw new Exception(String.format("服务器返回错误：[%d][%s]", new Object[] { Integer.valueOf(status), url }));
			}
			return method.getResponseBody();
		} catch (Exception ex) {
			throw new BaseException(String.format("发送数据失败：[%s][%s]", new Object[] { ex.getMessage(), url }), ex);
		} finally {
			method.releaseConnection();
		}
	}
}