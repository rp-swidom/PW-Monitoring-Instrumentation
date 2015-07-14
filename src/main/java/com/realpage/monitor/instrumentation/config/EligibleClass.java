package com.realpage.monitor.instrumentation.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="eligibleClass")
public class EligibleClass {
	@XmlAttribute(name="className")
	private String className;
	
	@XmlElementWrapper(name="eligibleMethods")
	@XmlElement(name="eligibleMethod")
	private ArrayList<EligibleMethod> eligibleMethods;
	
	@XmlAttribute(name="active")
	private Boolean active;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<EligibleMethod> getEligibleMethods() {
		return eligibleMethods;
	}

	public void setEligibleMethods(ArrayList<EligibleMethod> eligibleMethods) {
		this.eligibleMethods = eligibleMethods;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
