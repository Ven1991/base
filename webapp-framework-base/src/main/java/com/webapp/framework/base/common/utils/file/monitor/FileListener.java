package com.webapp.framework.base.common.utils.file.monitor;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileListener extends FileAlterationListenerAdaptor {
	private Logger log = LogManager.getLogger(FileListener.class);

	public static void main(String[] args) throws Exception {
		String rootDir = "C:/tmp";

		long interval = TimeUnit.SECONDS.toMillis(5L);

		FileAlterationObserver observer = new FileAlterationObserver(rootDir);

		observer.addListener(new FileListener());

		FileAlterationMonitor monitor = new FileAlterationMonitor(interval, new FileAlterationObserver[] { observer });

		monitor.start();
	}

	public void onFileCreate(File file) {
		this.log.info("[新建]:" + file.getAbsolutePath());
	}

	public void onFileChange(File file) {
		this.log.info("[修改]:" + file.getAbsolutePath());
	}

	public void onFileDelete(File file) {
		this.log.info("[删除]:" + file.getAbsolutePath());
	}

	public void onDirectoryCreate(File directory) {
		this.log.info("[新建]:" + directory.getAbsolutePath());
	}

	public void onDirectoryChange(File directory) {
		this.log.info("[修改]:" + directory.getAbsolutePath());
	}

	public void onDirectoryDelete(File directory) {
		this.log.info("[删除]:" + directory.getAbsolutePath());
	}

	public void onStart(FileAlterationObserver observer) {
		super.onStart(observer);
	}

	public void onStop(FileAlterationObserver observer) {
		super.onStop(observer);
	}
}