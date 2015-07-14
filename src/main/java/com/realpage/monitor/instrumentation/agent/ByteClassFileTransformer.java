package com.realpage.monitor.instrumentation.agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import com.realpage.monitor.instrumentation.util.Util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;


/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
public class ByteClassFileTransformer implements ClassFileTransformer {
	
	/* (non-Javadoc)
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])

	 * This class byte transformer adds a method annotation to all eligible classes and methods, and that annotation is of type
	 * com.realpage.annotations.MethodLogger which is intended to trigger an AOP injection into methods that have that
	 * annotation to instrument for logging.
	 * 
	 * We use javassist byte-code engineering to modify the eligible class bytecode.
	 */
	@SuppressWarnings("unchecked")
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		byte[] byteCode = classfileBuffer;
		
		boolean noisy = true;
		
		try {
			String normalizedClassName = className.replaceAll("/", ".");

			// if the class is eligible
			boolean isEligible = Util.isEligibleClass(normalizedClassName);
			
			if (isEligible == true) {
				
				if (noisy == true) {
					System.out.println("Eligible class " + normalizedClassName);
				}
				
				ClassPool cp = ClassPool.getDefault();

				CtClass cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
				
				ClassFile ccFile = cc.getClassFile();
				ccFile.setMajorVersion(51); // The correct value to use for Java 7
				ccFile.setMinorVersion(0);

				ConstPool constpool = ccFile.getConstPool();
				
				// Get all of the class methods
				CtMethod[] methods = cc.getDeclaredMethods();

				for (CtMethod method : methods) {
					if (noisy == true) {
						System.out.println("Method: " + method.getName());
					}
					
					if (Util.isEligibleMethod(normalizedClassName, method.getName()) == true) {
						
						// This is the new annotation we are going to add to this eligible method
						Annotation methodLoggerAnnotation = new Annotation("com.realpage.monitor.instrumentation.annotations.MethodLogger", constpool);
						
						// Add the new annotation member called "value" and set its value to extracted metadata from the
						// external config file.
						ArrayMemberValue member = new ArrayMemberValue(constpool);
						
						String[] values = Util.getMethodCategories(normalizedClassName, method.getName());
											
					    StringMemberValue[] members = new StringMemberValue[values.length];
					    
					    for (int i = 0; i < values.length; i++) {
					        members[i] = new StringMemberValue(values[i], constpool);
					    }
					    
					    member.setValue(members);
					    methodLoggerAnnotation.addMemberValue("value", member);
	
						List<AttributeInfo> methodAttributes = method.getMethodInfo().getAttributes();
								
						AnnotationsAttribute annotationsAttribute = null;
						
						// Look to see if there is already an annotations attribute on this method (which will exist if the
						// compiled method already has annotations on it
						for (AttributeInfo methodAttribute : methodAttributes) {
							if (methodAttribute instanceof AnnotationsAttribute) {
								if (noisy == true) {
									System.out.println("  Adding MethodLogger Annotation");
								}
								
								annotationsAttribute = (AnnotationsAttribute) methodAttribute;
								
								// Simply add the new annotation to the existing annotations attribute
								annotationsAttribute.addAnnotation(methodLoggerAnnotation);
								
								break;
							}
						}
						
						// If the method does not already have annotations on it then we need to go and create the
						// annotations attribute and add our new annotation to it, and then add the annotations attribute
						// to the method.
						if (annotationsAttribute == null) {
							annotationsAttribute = new AnnotationsAttribute(constpool, "RuntimeVisibleAnnotations");
							
							if (noisy == true) {
								System.out.println("  Adding MethodLogger Annotation");
							}
							
							// Add the annotation to the annotations attribute
							annotationsAttribute.addAnnotation(methodLoggerAnnotation);
							// Add the annotations attribute to the method
							method.getMethodInfo().addAttribute(annotationsAttribute);
						}
						
						// Print all of the method annotations to the console window
						if (noisy == true) {
							if (annotationsAttribute != null) {
								Annotation[] annotations = annotationsAttribute.getAnnotations();
								
								for (Annotation annotation : annotations) {
									System.out.println("  Annotation: " + annotation.getTypeName());
								}
							}
						}
					
					}
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream os = new DataOutputStream(bos);
				ccFile.write(os);
				byteCode = bos.toByteArray();
				os.close();
				bos.close();

				cc.detach();
			}

		} catch (Exception ex) {
			System.out.println("Error adding annotations to Class:" + ex.getMessage());
			if (noisy == true) {
				ex.printStackTrace();
			}
		}

		return byteCode;
	}
	
	static {
		try {
			Util.getMonitoringInstrumentation();
		}
		catch (Exception ex) {
			
		}
	}
		
}
