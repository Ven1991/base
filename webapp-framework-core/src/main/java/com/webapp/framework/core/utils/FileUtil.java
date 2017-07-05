package com.webapp.framework.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import com.webapp.framework.core.beans.FileInfo;
import com.webapp.framework.core.utils.StringUtil;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

public class FileUtil extends FileUtils {
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	public static List<String> ignoreList = new ArrayList();

	public static String getFileCharacterEnding(File file) {
		String fileCharacterEnding = "UTF-8";
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		detector.add(JChardetFacade.getInstance());
		try {
			Charset charset = detector.detectCodepage(file.toURL());
			if (charset != null)
				fileCharacterEnding = charset.name();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return fileCharacterEnding;
	}

	public static byte[] readFile(String filepath) throws IOException {
		FileInputStream fis = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(filepath);
			byte[] buf;
			while (true) {
				buf = new byte[8192];
				int c = fis.read(buf);
				if (c <= 0) {
					break;
				}
				bos.write(buf, 0, c);
			}

			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("read file=[" + filepath + "],exception:" + e.getLocalizedMessage());

			if (null != fis)
				fis.close();
			if (null != bos)
				bos.close();
		} finally {
			if (null != fis)
				fis.close();
			if (null != bos) {
				bos.close();
			}
		}
		return null;
	}

	public static void mkDir(String path) throws IOException {
		File f = new File(path);
		f.mkdirs();
	}

	public static void saveFile(String savePath, byte[] buf) throws IOException {
		FileOutputStream file = null;
		try {
			file = new FileOutputStream(savePath);
			file.write(buf);
		} catch (Exception ex) {
			logger.error("write file=[" + savePath + "],exception:" + ex.getLocalizedMessage());
		} finally {
			if (file != null)
				file.close();
		}
	}

	public static void exportZipFile(String filePath) throws IOException {
		BufferedInputStream bfs = null;
		byte[] data = new byte[1024];

		ZipOutputStream out = null;
		try {
			FileOutputStream dest = new FileOutputStream(filePath);
			List files = getFiles(filePath);
			out = new ZipOutputStream(new BufferedOutputStream(dest));
			for (int i = 0; i < files.size(); i++) {
				File file = (File) files.get(i);

				FileInputStream fis = new FileInputStream(file);
				bfs = new BufferedInputStream(fis);
				ZipEntry entry = new ZipEntry(file.getName());
				out.putNextEntry(entry);
				int count;
				while ((count = bfs.read(data, 0, 1024)) != -1) {
					out.write(data, 0, count);
				}
				bfs.close();
			}
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("write file=[" + filePath + "],exception:" + e.getLocalizedMessage());
		} catch (IOException e) {
			logger.error("write file=[" + filePath + "],exception:" + e.getLocalizedMessage());
		} finally {
			if (bfs != null) {
				bfs.close();
			}

			if (out != null)
				out.close();
		}
	}

	public static void main(String[] args) {
		System.out.println(getWebAppRealPath());
		System.out.println(getWebAppName());
	}

	public static File getFile(String path) {
		Assert.notNull(path);
		File file = new File(path);
		if (file.exists()) {
			return file;
		}
		URL url = FileUtils.class.getResource(path);
		if (null != url) {
			return new File(url.getPath());
		}
		File relativeFile = getRelativeFile(path);
		if (relativeFile.exists()) {
			return relativeFile;
		}
		return file;
	}

	public static File getRelativeFile(String name) {
		return getRelativeFile(null, name);
	}

	public static File getRelativeFile(String dir, String name) {
		String relativePath = getRelativeDir(dir);
		return new File(relativePath, name);
	}

	public static String getRelativeDir(String dir) {
		String relativePath = FileUtils.class.getResource("/").getPath();
		if (StringUtils.isNotBlank(dir)) {
			relativePath = relativePath + dir;
		}
		return relativePath;
	}

	public static List<File> getFiles(String dir) {
		return getFiles(dir, new ArrayList());
	}

	public static List<File> getFiles(String dir, List<File> list) {
		if (null == list) {
			list = new ArrayList();
		}
		if ((null == dir) || ("".equals(dir))) {
			return list;
		}
		File f = new File(dir);
		if (f.isFile()) {
			list.add(f);
			return list;
		}
		File[] files = f.listFiles();
		if (null == files) {
			logger.warn("dir=[" + dir + "],no files");
			return list;
		}
		for (File file : files) {
			if (file.isFile()) {
				list.add(file);
			} else
				getFiles(file.getPath(), list);
		}
		return list;
	}

	public static List<File> getFiles(File file, String suffix) {
		if (!exists(file)) {
			return null;
		}
		return getFiles(null, file.getPath(), suffix, true, true);
	}

	public static List<File> getFiles(String dirPath, String suffix) {
		return getFiles(null, dirPath, suffix, true, true);
	}

	public static List<File> getFiles(List<File> list, String dirPath, String suffix, boolean ignore, boolean sort) {
		if (null == list)
			list = new ArrayList();
		File file = getFile(dirPath);
		if (!exists(file)) {
			logger.warn("dir:{" + dirPath + "} is not exist");
			return list;
		}
		if (file.isFile()) {
			list.add(file);
			return list;
		}
		File[] files = file.listFiles();
		for (File f : files) {
			if ((!ignore) || (!isIgnoreFile(f))) {
				if (f.isFile()) {
					if ((StringUtils.isBlank(suffix)) || (f.getName().endsWith(suffix))) {
						list.add(f);
					}
				} else
					getFiles(list, f.getPath(), suffix, ignore, sort);
			}
		}
		if (sort) {
			Collections.sort(list, new Comparator() {
				public int compare(File o1, File o2) {
					return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
				}

				public int compare(Object o1, Object o2) {
					// TODO Auto-generated method stub
					return 0;
				}
			});
		}
		return list;
	}

	private static boolean isIgnoreFile(File file) {
		if (null == file) {
			return true;
		}
		for (String name : ignoreList) {
			if (name.equals(file.getName())) {
				return true;
			}
		}
		return false;
	}

	public static String getWebAppRealPath() {
		return HttpUtils.getWebAppRealPath();
	}

	public static String getWebAppName() {
		return HttpUtils.getWebAppName();
	}

	public static InputStream getInputStream(String path, String suffix) throws IOException {
		URL url = FileUtil.class.getResource(path);
		if (null == url) {
			File file = getFile(new File(path), suffix);
			if (!exists(file)) {
				return null;
			}
			return file.toURI().toURL().openStream();
		}
		return getFileStream(url, suffix);
	}

	private static InputStream getFileStream(URL url, String suffix) throws IOException {
		if ("jar".equals(url.getProtocol())) {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarEntry entry = con.getJarEntry();
			JarFile jarfile = con.getJarFile();
			return jarfile.getInputStream(getJarEntry(entry, jarfile, suffix));
		}
		File file = getFile(new File(url.getFile()), suffix);
		return file.toURI().toURL().openStream();
	}

	private static File getFile(File file, String suffix) {
		if (!exists(file)) {
			return file;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.getName().endsWith(suffix)) {
					file = f;
					break;
				}
			}
		}
		return file;
	}

	private static boolean exists(File file) {
		return (null != file) && (file.exists());
	}

	private static JarEntry getJarEntry(JarEntry entry, JarFile jarfile, String suffix) {
		if (!entry.isDirectory()) {
			return entry;
		}
		Enumeration enums = jarfile.entries();
		while (enums.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) enums.nextElement();
			String name = jarEntry.getName();
			if ((name.startsWith(entry.getName())) && (null != suffix) && (name.endsWith(suffix))) {
				entry = jarEntry;
				break;
			}
		}
		return entry;
	}

	public static void copyFile(String src, String dest) {
		if ((StringUtils.isBlank(src)) || (StringUtils.isBlank(dest))) {
			logger.error("拷贝文件输入参数不合法:src=[" + src + "],dest=[" + dest + "]");
			return;
		}
		try {
			File srcFile = new File(src);
			if (!exists(srcFile)) {
				logger.error("拷贝文件输入参数不合法,源文件不存在:srcFile=[" + srcFile + "]");
				return;
			}
			File destFile = new File(dest);
			if (destFile.isDirectory()) {
				if (destFile.getPath().equals(srcFile.getPath())) {
					logger.warn("拷贝文件操作忽略：目标路径和源路径相同");
					return;
				}
				destFile = new File(destFile.getPath(), srcFile.getName());
			}
			File dir = destFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			copyFile(srcFile, destFile);
		} catch (Exception e) {
			logger.error("拷贝文件异常:" + e.getLocalizedMessage());
		}
	}

	public static byte[] readFile(File file) throws IOException {
		byte[] buf = null;
		if (!file.exists()) {
			logger.warn("读取二进制文件失败，文件不存在!");
			return buf;
		}
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			int b = -1;
			while ((b = fis.read()) != -1) {
				baos.write(b);
			}
			buf = baos.toByteArray();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(baos);
		}
		return buf;
	}

	public static String getFileType(String fileName) {
		if (StringUtil.isNull(fileName)) {
			return null;
		}
		File file = new File(fileName);
		String name = file.getName();

		if (name.indexOf(".") <= 0) {
			return null;
		}

		String[] stb = name.split("\\.");
		return stb[(stb.length - 1)];
	}

	public static String getFileExtName(File file) {
		return getFileExtName(file.getName());
	}

	public static String getFileExtName(String fileName) {
		if (StringUtil.isNull(fileName))
			return null;
		if (fileName.indexOf(".") <= 0)
			return "";
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public static List<FileInfo> getDirFiles(String root) {
		Assert.notNull(root);
		List list = new ArrayList();
		File file = new File(root);
		if (!file.exists())
			return list;
		List<File> files = getFiles(root);
		for (File f : files) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFileName(f.getName());
			fileInfo.setFileLength(Long.valueOf(f.length()));
			fileInfo.setFilePath(root);
			list.add(fileInfo);
		}
		return list;
	}

	public static List<String> loadJarFileNames(URL jarFileName, String jarPath, String filters) {
		try {
			String fileName = getFileNameFromURL(jarFileName);
			if (null == fileName)
				return null;
			return loadJarFileNames(fileName, jarPath, filters);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static List<String> loadJarFileNames(String jarFileName, String jarPath, String filters) {
		try {
			if (null != jarPath) {
				if (jarPath.startsWith("/"))
					jarPath = jarPath.substring(1);
				if (!jarPath.endsWith("/")) {
					jarPath = jarPath + "/";
				}
			}
			JarInputStream zin = new JarInputStream(new FileInputStream(jarFileName));

			List list = new ArrayList();
			JarEntry entry;
			while ((entry = zin.getNextJarEntry()) != null)
				if (!entry.isDirectory()) {
					String name = entry.getName();
					if (((null == jarPath) || (name.startsWith(jarPath))) && (name.endsWith(filters))) {
						list.add(name);
					}
				}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] loadJarFileData(URL jarFileName, String filePath) {
		try {
			String fileName = getFileNameFromURL(jarFileName);
			if (null == fileName)
				return null;
			return loadJarFileData(fileName, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] loadJarFileData(String jarFileName, String filePath) {
		if ((StringUtil.isNull(jarFileName)) || (StringUtil.isNull(filePath))) {
			return null;
		}
		if (filePath.startsWith("/")) {
			filePath = filePath.substring(1);
		}
		try {
			JarInputStream zin = new JarInputStream(new FileInputStream(jarFileName));
			JarEntry entry;
			while ((entry = zin.getNextJarEntry()) != null)
				if (!entry.isDirectory()) {
					String name = entry.getName();
					if (name.endsWith(filePath)) {
						int read = 0;
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						while ((read = zin.read(buffer)) != -1) {
							bs.write(buffer, 0, read);
						}
						return bs.toByteArray();
					}
				}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static String getFileNameFromURL(URL jarFileName) {
		if (null == jarFileName)
			return null;
		try {
			String fileName = jarFileName.toString();

			if (fileName.toLowerCase().indexOf(".jar!/") > 0) {
				fileName = fileName.substring(0, fileName.indexOf(".jar!/") + 4);
			}
			if (fileName.startsWith("jar:")) {
				fileName = fileName.substring("jar:".length());
			}
			jarFileName = new URL(fileName);
			File file = new File(jarFileName.toURI());
			return file.getPath();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String getMavenVersionJAR(String jarFilePath) {
		if (StringUtil.isNull(jarFilePath)) {
			return null;
		}
		try {
			JarInputStream zin = new JarInputStream(new FileInputStream(jarFilePath));

			String fileData = null;
			JarEntry entry;
			while ((entry = zin.getNextJarEntry()) != null)
				if (!entry.isDirectory()) {
					String name = entry.getName();
					if (name.endsWith("/pom.properties")) {
						int read = 0;
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						while ((read = zin.read(buffer)) != -1) {
							bs.write(buffer, 0, read);
						}
						fileData = new String(bs.toByteArray(), "utf-8");
					}
				}
			if (null == fileData) {
				return null;
			}

			InputStream is = new ByteArrayInputStream(fileData.getBytes());
			Properties p = new Properties();
			p.load(is);
			String version = p.getProperty("version");
			is.close();
			return version;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String getMavenVersionWEB() {
		String appPath = getWebAppRealPath();
		appPath = appPath + "/META-INF/maven";
		File dirMaven = new File(appPath);
		if (!dirMaven.exists()) {
			return null;
		}
		for (String file : dirMaven.list()) {
			String filePath = appPath + "/" + file;
			File subDir = new File(filePath);
			if (subDir.isDirectory()) {
				for (String subDirFile : subDir.list()) {
					String subFilePath = filePath + "/" + subDirFile;
					File subFile = new File(subFilePath);
					if (subFile.isDirectory()) {
						String pFilePath = subFilePath + "/pom.properties";
						File pFile = new File(pFilePath);
						if (pFile.exists()) {
							Properties p = new Properties();
							try {
								p.load(getFileStream(pFilePath));
							} catch (IOException e) {
								continue;
							}
							return p.getProperty("version");
						}
					}
				}
			}
		}
		return null;
	}

	public static void delFile(String filePathName) {
		File file = new File(filePathName);
		if (file.exists())
			file.delete();
	}

	public static InputStream getFileStream(String path) {
		InputStream inStream = null;
		try {
			File file = new File(path);

			if (file.exists()) {
				inStream = new FileInputStream(file);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return inStream;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static boolean deleteDir(String dir) {
		return deleteDir(new File(dir));
	}
}