package com.webapp.framework.base.common.utils.zip;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	public static void fileZip(String filepath, String ifilename, String ofilename) throws Exception {
		File f = new File(filepath + "/" + ifilename);
		ZipEntry ze = new ZipEntry(getAbsFileName(filepath, f));
		ze.setSize(f.length());
		ze.setTime(f.lastModified());
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(ofilename));
		zos.putNextEntry(ze);
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = is.read(buf, 0, 1024)) != -1) {
			zos.write(buf, 0, readLen);
		}
		is.close();
		zos.close();
	}

	private static String getAbsFileName(String baseDir, File realFileName) {
		File real = realFileName;
		File base = new File(baseDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base)) {
				break;
			}
			ret = real.getName() + "/" + ret;
		}

		return ret;
	}

	public static byte[] compress(byte[] buf) throws IOException {
		if ((buf == null) || (buf.length == 0)) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(buf);
		gzip.close();
		return out.toByteArray();
	}

	public static byte[] decompress(byte[] buf) throws IOException {
		if ((buf == null) || (buf.length == 0)) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(buf);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
}