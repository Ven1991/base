package com.webapp.framework.core.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UrlFilters")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlFilters {

	@XmlElement(name = "Url")
	private List<CanAccessActionBean> urlFilters;

	public List<CanAccessActionBean> getUrlFilters() {
		return this.urlFilters;
	}

	public void setUrlFilters(List<CanAccessActionBean> urlFilters) {
		this.urlFilters = urlFilters;
	}
}