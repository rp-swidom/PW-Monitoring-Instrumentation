package com.realpage.monitor.instrumentation.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
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
@XmlRootElement(name = "monitoringInstrumentation")
public class MonitoringInstrumentation {

	@XmlElement(name="eligibles")
	private Eligibles eligibles;
	
	@XmlElementWrapper(name="monitorDirectives")
	@XmlElement(name="monitorDirective")
	private ArrayList<MonitorDirective> monitorDirectives;

	@XmlElementWrapper(name="monitorCategories")
	@XmlElement(name="monitorCategory")
	private ArrayList<MonitorCategory> monitorCategories;

	public Eligibles getEligibles() {
		return eligibles;
	}

	public void setEligibles(Eligibles eligibles) {
		this.eligibles = eligibles;
	}

	public ArrayList<MonitorCategory> getMonitorCategories() {
		return monitorCategories;
	}

	public void setMonitorCategories(ArrayList<MonitorCategory> monitorCategories) {
		this.monitorCategories = monitorCategories;
	}

	public ArrayList<MonitorDirective> getMonitorDirectives() {
		return monitorDirectives;
	}

	public void setMonitorDirectives(ArrayList<MonitorDirective> monitorDirectives) {
		this.monitorDirectives = monitorDirectives;
	}
	
}
