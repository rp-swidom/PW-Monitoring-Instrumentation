package com.realpage.monitor.instrumentation.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
@XmlRootElement(name="eligibles")
public class Eligibles {

	@XmlElementWrapper(name="eligibleClasses")
	@XmlElement(name="eligibleClass")
	private ArrayList<EligibleClass> eligibleClasses;

	public ArrayList<EligibleClass> getEligibleClasses() {
		return eligibleClasses;
	}

	public void setEligibleClasses(ArrayList<EligibleClass> eligibleClasses) {
		this.eligibleClasses = eligibleClasses;
	}
	
}
