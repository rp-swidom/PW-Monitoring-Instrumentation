package com.realpage.monitor.instrumentation.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
public class Agent {

	private static volatile Instrumentation instrumentation;
	
	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}

	public static void premain(String args, Instrumentation inst) throws Exception {
        instrumentation = inst;
        instrumentation.addTransformer(new ByteClassFileTransformer());
    }
	
	public static void agentmain(String args, Instrumentation inst) throws Exception {
        instrumentation = inst;
        instrumentation.addTransformer(new ByteClassFileTransformer());
    }
	
}
