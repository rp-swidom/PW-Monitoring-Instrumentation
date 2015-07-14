package com.realpage.monitor.instrumentation.annotations;

import java.lang.annotation.Documented; 
import java.lang.annotation.ElementType; 
import java.lang.annotation.Inherited; 
import java.lang.annotation.Retention; 
import java.lang.annotation.RetentionPolicy; 
import java.lang.annotation.Target; 

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
@Documented 
@Target(ElementType.METHOD) 
@Inherited 
@Retention(RetentionPolicy.RUNTIME) 
public @interface MethodLogger {
	String[] value();
}
