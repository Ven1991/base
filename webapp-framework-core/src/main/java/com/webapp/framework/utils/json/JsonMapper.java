package com.webapp.framework.utils.json;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.webapp.framework.utils.tools.StringUtil;

public class JsonMapper {
	private static Logger logger = LogManager.getLogger(JsonMapper.class);
	private ObjectMapper mapper;

	public JsonMapper() {
		this(JsonInclude.Include.ALWAYS);
	}

	public JsonMapper(JsonInclude.Include include) {
		this.mapper = new ObjectMapper();

		this.mapper.setSerializationInclusion(include);

		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public static boolean isJson(String json) {
		try {
			if ((StringUtil.isNull(json)) || (json.length() <= 1))
				return false;
			new JsonParser().parse(json);
			new JSONObject(json);
		} catch (Exception e) {
			try {
				new JSONArray(json);
			} catch (JSONException e1) {
				return false;
			}
		}
		return true;
	}
}