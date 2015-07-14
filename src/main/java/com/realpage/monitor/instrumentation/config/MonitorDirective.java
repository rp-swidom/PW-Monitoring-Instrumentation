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
@XmlRootElement(name="monitorDirective")
public class MonitorDirective {

	@XmlAttribute(name="name")
	private String directiveName;
	
	@XmlAttribute(name="value")
	private String directiveValue;

	public String getDirectiveName() {
		return directiveName;
	}

	public void setDirectiveName(String directiveName) {
		this.directiveName = directiveName;
	}

	public String getDirectiveValue() {
		return directiveValue;
	}

	public void setDirectiveValue(String directiveValue) {
		this.directiveValue = directiveValue;
	}
}
