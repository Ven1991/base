package com.webapp.framework.core.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
public class CanAccessActionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "namespace")
	private String namespace = "";

	@XmlAttribute(name = "method")
	private String method = "";

	@XmlAttribute(name = "checkspace")
	private String checkspace = "";

	@XmlAttribute(name = "isNeedUserLogin")
	private boolean isNeedUserLogin = false;

	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isNeedUserLogin() {
		return this.isNeedUserLogin;
	}

	public void setNeedUserLogin(boolean isNeedUserLogin) {
		this.isNeedUserLogin = isNeedUserLogin;
	}

	public String getCheckspace() {
		return this.checkspace;
	}

	public void setCheckspace(String checkspace) {
		this.checkspace = checkspace;
	}
}