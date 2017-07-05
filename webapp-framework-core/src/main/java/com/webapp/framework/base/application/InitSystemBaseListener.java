package com.webapp.framework.base.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class InitSystemBaseListener implements ApplicationListener<ApplicationEvent> {
	protected Logger log = LogManager.getLogger(getClass());
	private static boolean isStart = false;

	public void onApplicationEvent(ApplicationEvent event) {
		if (isStart)
			return;
		isStart = true;
		init();
	}

	protected abstract void init();
}