package com.realpage.monitor.instrumentation.aspects;

import com.realpage.monitor.instrumentation.util.Util;
import com.realpage.monitor.instrumentation.annotations.MethodLogger;

import org.aspectj.lang.reflect.MethodSignature;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
public aspect MethodAnnotationAspect {

	private static Logger logger = LoggerFactory.getLogger(com.realpage.monitor.instrumentation.annotations.MethodLogger.class);

	pointcut annotatedLogger() :
		execution(* *(..)) && @annotation(com.realpage.monitor.instrumentation.annotations.MethodLogger) && 
			if(Util.methodAnnotationMatches(thisJoinPoint.getSignature().getDeclaringType(),
					MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod()));
		
	Object around(): annotatedLogger() {
				
		Object[] arguments = thisJoinPoint.getArgs();
		
		StringBuffer buff = new StringBuffer();
		
	    for (int i = 0; i < arguments.length; i++){
	        Object argument = arguments[i];
	        if (argument != null) {
	        	if (buff.toString().equals("") == false) {
	        		buff.append(",");
	        	}
	        	buff.append(argument.getClass().toString() + ":" + argument);
	        }
	    }
	    	    
		String correlationID = Util.generateCorrelationID();
		
		logger.info("Starting: {} {}({})",
				new Object[]{correlationID, MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod().getName(), buff.toString()});
		
		Object result = null;
		
		long end = -1;
		
		long start = System.currentTimeMillis();
		
		try {
			result = proceed();
			
			end = System.currentTimeMillis();
		}
		catch (Throwable ex) {
			end = System.currentTimeMillis();
			
			logger.info("Method Failure: {} {}({}) at {}",
					new Object[]{correlationID, MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod().getName(), buff.toString(),
					(end - start)});
		}

		boolean outputResults = Util.isOutputResults(thisJoinPoint.getSignature().getDeclaringType(), MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod().getName());
		
		if (outputResults == true) {
			logger.info("Completed: {} {}({}) Result: {} in {} msec(s)",
					new Object[]{correlationID, MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod().getName(), buff.toString(), result,
					(end - start)});
		}
		else {
			logger.info("Completed: {} {}({}) in {} msec(s)",
					new Object[]{correlationID, MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod().getName(), buff.toString(),
					(end - start)});			
		}
		
		return result;
	}
}
