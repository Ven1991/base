package com.webapp.framework.utils.file.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.webapp.framework.base.exception.BaseException;
import com.webapp.framework.utils.file.upload.bean.UploadFileBean;
import com.webapp.framework.utils.tools.StringUtil;

public class FileUploadTools {
	protected static Logger log = LogManager.getLogger(FileUploadTools.class);
	private static final String ALLOW_UP_FILE_TYPE = "*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;";
	private static final Long ALLOW_MAX_FILE_SIZE = Long.valueOf(104857600L);

	public static UploadFileBean loadWebUploadFile(HttpServletRequest request) throws IOException {
		return loadWebUploadFile(request,
				"*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;",
				ALLOW_MAX_FILE_SIZE);
	}

	public static UploadFileBean loadWebUploadFile(HttpServletRequest request, String FileType) throws IOException {
		return loadWebUploadFile(request, FileType, ALLOW_MAX_FILE_SIZE);
	}

	public static UploadFileBean loadWebUploadFile(HttpServletRequest request, Long FileSize) throws IOException {
		return loadWebUploadFile(request,
				"*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;",
				FileSize);
	}

	public static UploadFileBean loadWebUploadFile(HttpServletRequest request, String fileType, Long fileSize)
			throws IOException {
		if (null == request) {
			return null;
		}

		MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());

		MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

		MultipartFile file = multipartRequest.getFile("Filedata");
		String inputName = null;

		if (file == null) {
			DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
			MultiValueMap map = r.getMultiFileMap();
			if ((null == map) || (map.isEmpty())) {
				BaseException.throwException("请选择需要上传的文件");
			}
			if (map.size() > 1) {
				BaseException.throwException("当前系统不支持多文件上传");
			}
			Object[] keys = map.keySet().toArray();
			String key = (String) keys[0];
			inputName = key;

			if (null == map.get(key)) {
				BaseException.throwException("请选择需要上传的文件");
			}
			file = (MultipartFile) ((List) map.get(key)).get(0);

			if (null == file) {
				BaseException.throwException("请选择需要上传的文件");
			}
		}
		String fileName = file.getOriginalFilename();
		log.info("接收上传文件[" + fileName + "]");

		if (StringUtil.isNull(fileName)) {
			BaseException.throwException("文件上传失败");
		}
		String extName = getExtension(fileName);
		if (StringUtil.isNull(extName)) {
			BaseException.throwException("文件上传失败");
		}
		byte[] buf = file.getBytes();

		if (null == buf) {
			BaseException.throwException("文件上传失败");
		}
		if (StringUtil.isBlank(fileType)) {
			fileType = "*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;";
		}
		if (fileType.indexOf(extName.toLowerCase()) < 0) {
			BaseException.throwException("只能上传[{}]格式的文件。", new Object[] { fileType });
		}
		return new UploadFileBean(buf, fileName, Long.valueOf(file.getSize()), inputName);
	}

	public static List<UploadFileBean> webUploadFiles(HttpServletRequest request) throws IOException {
		return webUploadFiles(request,
				"*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;",
				ALLOW_MAX_FILE_SIZE);
	}

	public static List<UploadFileBean> webUploadFiles(HttpServletRequest request, String fileType) throws IOException {
		return webUploadFiles(request, fileType, ALLOW_MAX_FILE_SIZE);
	}

	public static List<UploadFileBean> webUploadFiles(HttpServletRequest request, Long fileSize) throws IOException {
		return webUploadFiles(request,
				"*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;",
				fileSize);
	}

	public static List<UploadFileBean> webUploadFiles(HttpServletRequest request, String fileType, Long fileSize)
			throws IOException {
		MultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		List listbean = new ArrayList();

		if (!multipartResolver.isMultipart(request)) {
			return listbean;
		}

		MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);

		Iterator iter = multiRequest.getFileNames();

		if (iter.hasNext()) {
			while (iter.hasNext()) {
				int pretime = (int) System.currentTimeMillis();

				String inputName = (String) iter.next();
				MultipartFile file = multiRequest.getFile(inputName);

				listbean.add(loadFileFromRequest(inputName, file, fileSize, fileType));

				int finaltime = (int) System.currentTimeMillis();

				String myFileName = file.getOriginalFilename();
				log.info("接收上传文件[" + myFileName + "]，用时{" + (finaltime - pretime) + "ms}。");
			}
		}

		DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
		MultiValueMap map = r.getMultiFileMap();
		if ((null == map) || (map.isEmpty())) {
			BaseException.throwException("请选择需要上传的文件");
		}
		Object[] keys = map.keySet().toArray();
		// int finaltime = keys;
		int myFileName = keys.length;
		for (int str1 = 0; str1 < myFileName; str1++) {
			Object key = keys[str1];

			int pretime = (int) System.currentTimeMillis();
			if (null == map.get(key)) {
				BaseException.throwException("请选择需要上传的文件");
			}
			MultipartFile file = (MultipartFile) ((List) map.get(key)).get(0);

			String inputName = (String) key;
			listbean.add(loadFileFromRequest(inputName, file, fileSize, fileType));

			int finaltime = (int) System.currentTimeMillis();

			String myFileNames = file.getOriginalFilename();
			log.info("接收上传文件[" + myFileNames + "]，用时{" + (finaltime - pretime) + "ms}。");
		}

		return listbean;
	}

	private static UploadFileBean loadFileFromRequest(String inputName, MultipartFile file, Long fileSize,
			String fileType) throws IOException {
		if ((null == file) || (file.isEmpty())) {
			return new UploadFileBean("没有文件上传", inputName);
		}

		String myFileName = file.getOriginalFilename();

		if ((null == myFileName) || (myFileName.trim() == "")) {
			return new UploadFileBean("接收上传文件不存在", inputName);
		}
		if (file.getSize() > fileSize.longValue()) {
			log.info("接收上传文件[" + myFileName + "],超出设定大小。");
			return new UploadFileBean("接收上传文件[" + myFileName + "],超出设定大小", inputName);
		}

		String extName = getExtension(myFileName);

		if (StringUtil.isNull(fileType)) {
			fileType = "*.mpg;*.wmv;*.mp4;*.jpeg;*.png;*.jpg;*.gif;*.bmp;*.xls;*.xlsx;*.docx;*.ppt;*.pptx;*.txt;*.zip;";
		}

		if (fileType.indexOf(extName.toLowerCase()) < 0) {
			log.info("接收上传文件[" + myFileName + "],只能上传[" + fileType + "]格式的文件。");
			return new UploadFileBean("接收上传文件[" + myFileName + "],只能上传[" + fileType + "]格式的文件", inputName);
		}

		return new UploadFileBean(file.getBytes(), myFileName, Long.valueOf(file.getSize()), inputName);
	}

	private static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}