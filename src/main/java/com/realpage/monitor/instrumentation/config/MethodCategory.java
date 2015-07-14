package com.realpage.monitor.instrumentation.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="methodCategory")
public class MethodCategory {

	@XmlAttribute(name="category")
	private String categoryName;

	@XmlAttribute(name="active")
	private Boolean active;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
