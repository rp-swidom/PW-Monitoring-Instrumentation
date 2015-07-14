package com.realpage.monitor.instrumentation.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="eligibleMethod")
public class EligibleMethod {

	@XmlAttribute(name="methodName")
	private String methodName;
	
	@XmlElementWrapper(name="methodCategories")
	@XmlElement(name="methodCategory")
	private ArrayList<MethodCategory> methodCategories;

	@XmlAttribute(name="outputResults")
	private Boolean outputResults;
	
	@XmlAttribute(name="active")
	private Boolean active;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public ArrayList<MethodCategory> getMethodCategories() {
		return methodCategories;
	}

	public void setMethodCategories(ArrayList<MethodCategory> methodCategories) {
		this.methodCategories = methodCategories;
	}
	
	public Boolean getOutputResults() {
		return outputResults;
	}

	public void setOutputResults(Boolean outputResults) {
		this.outputResults = outputResults;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
