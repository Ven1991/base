package com.webapp.framework.base.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.HtmlUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webapp.framework.base.common.utils.BeanUtil;
import com.webapp.framework.base.common.utils.excel.ExcelUtil;
import com.webapp.framework.base.common.utils.page.Page;
import com.webapp.framework.base.common.utils.page.PageQuery;
import com.webapp.framework.core.beans.HttpStackManager;
import com.webapp.framework.core.exception.BaseException;
import com.webapp.framework.core.utils.HttpUtils;
import com.webapp.framework.core.utils.JsonMapper;
import com.webapp.framework.core.utils.SerializableUtil;
import com.webapp.framework.core.utils.StringUtil;

import net.sf.json.JSONObject;

public class BaseController {
	protected Logger log = LogManager.getLogger(getClass());

	public static final String ERROR_MESSAGE_IN_MAP_KEY = "_error";
	public static final String SUCCESS_MESSAGE_IN_MAP_KEY = "_success";

	protected String redirect(String url) {
		return String.format("redirect:%s", new Object[] { url });
	}

	@ModelAttribute
	protected void setModelMap(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		HttpStackManager.initNewData(request, response, modelMap);
	}

	public void setBean(String name, Object value) {
		ModelMap modelMap = HttpStackManager.getModelMap();
		if (null == modelMap)
			BaseException.throwException("ModelMap is null.");
		modelMap.addAttribute(name, value);
	}

	protected void success(String msg, Object[] args) {
		if (null == msg) {
			return;
		}
		if ((null != args) && (args.length > 0)) {
			msg = StringUtil.replace(msg, args);
		}

		setBean("_success", msg.replaceAll("\"", "\\\\\""));
	}

	protected void success(String msg) {
		success(msg, new Object[0]);
	}

	protected void error(String msg, Object[] args) {
		if (null == msg) {
			return;
		}
		if ((null != args) && (args.length > 0)) {
			msg = StringUtil.replace(msg, args);
		}

		setBean("_error", msg.replaceAll("\"", "\\\\\""));
	}

	protected void error(String msg) {
		error(msg, new Object[0]);
	}

	protected String okAjaxResult(Object value) {
		Map map = new HashMap();
		map.put("code", Integer.valueOf(0));
		map.put("data", value);
		outJsonObject(map);

		return null;
	}

	protected String errAjaxResult(String errMsg) {
		Map map = new HashMap();
		map.put("code", Integer.valueOf(1));
		map.put("message", errMsg);
		outJsonObject(map);
		return null;
	}

	protected <T> T getForm(Class<T> clas) {
		try {
			return copyProperties(clas, getParameterMap(true));
		} catch (Exception e) {
			this.log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	protected <T> T getForm(Class<T> clas, boolean filter) {
		try {
			return copyProperties(clas, getParameterMap(filter));
		} catch (Exception e) {
			this.log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	protected void copyProperties(Object target, Object source) {
		BeanUtil.copyProperties(target, source);
	}

	protected <T> T copyProperties(Class<T> destClass, Object orig) {
		return BeanUtil.copyProperties(destClass, orig);
	}

	protected Map<String, Object> getParameterMap(boolean escape) {
		Map map = new LinkedHashMap();
		HttpServletRequest req = getRequest();
		Enumeration names = req.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String[] values = escape ? getParameterValues(name) : req.getParameterValues(name);

			if (name.endsWith("[]")) {
				name = name.substring(0, name.getBytes().length - 2);
			}

			if ((null == values) || (values.length == 0)) {
				map.put(name, values);
			} else if (values.length == 1) {
				map.put(name, values[0]);
			} else {
				StringBuilder builder = new StringBuilder();
				for (String value : values) {
					builder.append(new StringBuilder().append("'").append(value).append("',").toString());
				}
				builder = builder.deleteCharAt(builder.length() - 1);
				map.put(name, builder.toString());
			}
		}
		return map;
	}

	protected String[] getParameterValues(String name) {
		return getParameterValues(name, Whitelist.basic());
	}

	protected String[] getParameterValues(String name, Whitelist whitelist) {
		return HttpUtils.getParameterValues(name, whitelist);
	}

	protected String getParameter(String name) {
		return (String) HttpUtils.getParameter(name, "");
	}

	protected <T> T getParameter(String name, Object sDefault) {
		return getParameter(name, sDefault, Whitelist.basic());
	}

	protected <T> T getParameter(String name, Object sDefault, Whitelist whitelist) {
		return getParameter(name, sDefault, whitelist, false);
	}

	protected <T> T getParameter(String name, Object sDefault, Whitelist whitelist, boolean escape) {
		String value = getRequest().getParameter(name);
		if (null == value) {
			return (T) sDefault;
		}
		String val = clean(name, value, whitelist);
		return (T) convert(val, sDefault.getClass());
	}

	protected String clean(String name, String value, Whitelist whitelist) {
		return clean(name, value, whitelist, false);
	}

	protected String clean(String name, String value, Whitelist whitelist, boolean escapeHTML) {
		if ((value.isEmpty()) || (null == whitelist) || (JsonMapper.isJson(value))) {
			return value;
		}
		String val = Jsoup.clean(value, whitelist);
		if (escapeHTML) {
			val = HtmlUtils.htmlEscape(val);
		}
		if (!val.equals(value)) {
			this.log.info(new StringBuilder().append("parameter(").append(name).append(") value is unsafe:")
					.append(value).append(" ,be cleaned : ").append(val).toString());
		}
		return val;
	}

	protected <T> T convert(Object o, Class<T> clas) {
		try {
			return (T) ConvertUtils.convert(o.toString(), clas);
		} catch (Exception e) {
			this.log.error(e.getLocalizedMessage(), e);
		}
		return (T) o;
	}

	public HttpServletRequest getRequest() {
		return HttpStackManager.getRequest();
	}

	public HttpServletResponse getResponse() {
		return HttpStackManager.getResponse();
	}

	protected HttpSession getSession() {
		HttpServletRequest request = getRequest();
		if (null == request)
			return null;
		return request.getSession();
	}

	protected void outJsonObject(Object o) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
		String msg = gson.toJson(o);
		outString(msg);
	}

	protected void outString(String msg) {
		HttpServletResponse response = getResponse();
		if (null == response) {
			BaseException.throwException("response is null");
		}
		response.setContentType("text/html;charset=utf-8");
		try {
			String callback = getParameter("jsoncallback");
			if (StringUtil.isNotNull(callback))
				response.getWriter()
						.write(new StringBuilder().append(callback).append("(").append(msg).append(")").toString());
			else
				response.getWriter().write(msg);
		} catch (Exception localException) {
		}
	}

	protected void outJsonArray(Object o) {
		outJsonObject(o);
	}

	protected <T> T getUser() {
		HttpSession session = getSession();
		if (null == session)
			return null;
		Object o = session.getAttribute("LOGIN_USER_IN_SESSION_KEY");
		if (null == o) {
			return null;
		}
		return (T) o;
	}

	// protected String getUserId() {
	// BaseUserForm user = (BaseUserForm)getUser();
	// if (null == user)
	// return null;
	// return user.getUserId();
	// }

	protected void writeLog(Object fphm, String context, boolean isStart) {
		if (isStart)
			context = "开始尝试" + context + "....";
		else {
			context = context + "成功。";
		}
		writeTextLog(fphm, context);
	}

	protected String serializable(Object o) {
		try {
			String str = SerializableUtil.serializable(o);
			return str.replaceAll("\r", "").replaceAll("\n", "");
		} catch (Exception e) {
			this.log.error(e.getMessage());
		}
		return null;
	}

	protected Object unserializable(String source) {
		try {
			return SerializableUtil.unserializable(source);
		} catch (Exception e) {
			throw new BaseException("反序列化错误", e);
		}
	}

	protected void writeTextLog(Object fphm, String context) {
		writeTextLog(fphm, context, new Object[0]);
	}

	protected void writeTextLog(Object fphm, String context, Object[] args) {
		if ((null != args) && (args.length > 0)) {
			context = StringUtil.replace(context, args);
		}

		String modelName = (String) HttpStackManager.getData("com.golden.core.base.action.BaseAction.modelName");
		this.writeLog(getUser(), modelName, context, null == fphm ? "" : fphm.toString());
	}

	protected void writeLog(Object user, String mkname, String message, String fphm) {
		// LogInfo model = new LogInfo();
		// if (null != user)
		// model.LoadUser(user);
		// else {
		// try
		// {
		// model.setUserip(HttpUtils.getIpAddr());
		// } catch (Exception e) {
		// model.setUserip("");
		// }
		//
		// }
		//
		// String paltform = RunTime.getTableShortName();
		// if (null == paltform)
		// paltform = "ex";
		// model.setRunType(paltform.toString());
		// model.setOpmodule(mkname);
		// model.setLogtitle(message);
		// model.setOpfphm(fphm);
		// model.setRq(new Date());
		//
		// final String log_path = LogFileQueue.LOG_PATH + File.separator +
		// paltform;
		// final String sql = model.getSql();
		// new Thread() {
		// public synchronized void run() {
		// BaseOptLogActionImpl.this.queue.write(sql, log_path);
		// }
		// }
		// .start();
	}

	protected void writeErrorLog(Object fphm, String context, Throwable e) {
		context = context + "，失败:" + e.getMessage();
		try {
			Map data = getParameterMap(true);
			String json2 = JSONObject.fromObject(data).toString();

			this.log.info(json2);
		} catch (Exception localException) {
		}
		this.log.error(context, e);

		writeTextLog(fphm, context);
	}

	public void setModelName(String modelName) {
		HttpStackManager.putData("com.golden.core.base.action.BaseAction.modelName", modelName);
	}

	public String getModelId() {
		String modelId = (String) HttpStackManager.getData("com.golden.core.base.action.BaseAction.modelId");
		return modelId;
	}

	public void setModelId(String modelId) {
		HttpStackManager.putData("com.golden.core.base.action.BaseAction.modelId", modelId);
	}

	// protected void setModelMap(HttpServletRequest request,
	// HttpServletResponse response, ModelMap modelMap)
	// {
	// super.setModelMap(request, response, modelMap);
	// String modelId = getModelId();
	// setBean("CUR_MODEL_ID", modelId);
	// }

	protected void bindExcel(String filename, String[] beannames, String[] titles, List list, boolean t)
			throws Exception {
		ExcelUtil excel = new ExcelUtil(titles);
		excel.setBeannames(beannames);
		HSSFWorkbook wb = excel.doExportXLS(list, "sheet1", t);
		HttpServletResponse response = getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("content-disposition", "attachment;filename=\"" + filename + ".xls\"");

		wb.write(response.getOutputStream());
		response.getOutputStream().flush();
		response.flushBuffer();
		response.getOutputStream().close();
	}

	protected void bindExcelNew(String filename, String[] beannames, String[] titles, List list, boolean t)
			throws Exception {
		ExcelUtil excel = new ExcelUtil(titles);
		excel.setBeannames(beannames);
		HttpServletResponse response = getResponse();

		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-disposition",
				"attachment;filename=\"" + new String(filename.getBytes("gb2312"), "ISO8859-1") + ".xlsx\"");

		excel.doExportXLSNew(list, filename, t, response.getOutputStream());
		response.getOutputStream().flush();
		response.flushBuffer();
		response.getOutputStream().close();
	}

	protected void bindFile(String fileName, String path) {
		InputStream inStream = null;
		try {
			File file = new File(path);

			if (file.exists()) {
				HttpServletResponse response = getResponse();
				response.setContentType("application/vnd.ms-excel");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("content-disposition",
						"attachment;filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + "\"");

				inStream = new FileInputStream(file);
				OutputStream os = response.getOutputStream();
				byte[] b = new byte[2048];
				int length;
				while ((length = inStream.read(b)) > 0) {
					os.write(b, 0, length);
				}
				inStream.close();
				os.flush();
				os.close();
			}
		} catch (Exception localException) {
		}
	}

	protected void savePage(Page<?> page, PageQuery form) {
		getRequest().setAttribute("page", page);
		getRequest().setAttribute("form", form);
		getRequest().setAttribute("query", form);
	}
}