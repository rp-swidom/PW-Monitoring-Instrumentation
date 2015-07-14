package com.realpage.monitor.instrumentation.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.realpage.monitor.instrumentation.annotations.MethodLogger;
import com.realpage.monitor.instrumentation.config.Eligibles;
import com.realpage.monitor.instrumentation.config.EligibleClass;
import com.realpage.monitor.instrumentation.config.EligibleMethod;
import com.realpage.monitor.instrumentation.config.MethodCategory;
import com.realpage.monitor.instrumentation.config.MonitorCategory;
import com.realpage.monitor.instrumentation.config.MonitorDirective;
import com.realpage.monitor.instrumentation.config.MonitoringInstrumentation;

/**
 * @author SWidom
 * 
 * 7/13/2015
 *
 */
public class Util {
	
	private static MonitoringInstrumentation monitoringInstrumentation = null;
	
	private static volatile Thread watchingThread;
	
	private static boolean noisy = false;
	
	public static void watchConfigFile() {
		
		watchingThread = new Thread() {
			public void run() {
				String fileName = "";
				
				try {
					String configFilePath = System.getProperty("rpMonitoringInstrumentationConfigFilePath");
					
					int index = configFilePath.replace("\\", "/").lastIndexOf("/");
					fileName = configFilePath.substring(index + 1);
					
					File parentDirectory = new File(configFilePath.substring(0, index));
					Path directoryPath = parentDirectory.toPath();

					try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
					    final WatchKey watchKey = directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
					    
					    System.out.println("Successfully started file watcher on file " + fileName);

					    while (true) {
					        final WatchKey wk = watchService.take();
					        for (WatchEvent<?> event : wk.pollEvents()) {
					            //we only register "ENTRY_MODIFY" so the context is always a Path.
					            final Path changed = (Path) event.context();
				                System.out.println(changed + " has changed");
				                
				                String changedName = changed.getFileName().toString();
				                
					            if (changedName.equals(fileName)) {
					            	System.out.println("Reloading config file " + fileName);
					                Util.setMonitoringInstrumentation(null);
					            }
					        }
					        // reset the key
					        boolean valid = wk.reset();
					        if (!valid) {
					            System.out.println("Key has been unregistered");
					        }
					    }
					    
					}
				}
				catch(Exception ex) {
					watchingThread.interrupt();
					watchingThread = null;
					
					if (noisy == true) {
						System.out.println("Error watching config file " + fileName);
						
						ex.printStackTrace();
					}
				}
			}
		};
		watchingThread.start();
		
	}
	
	public static void setMonitoringInstrumentation(MonitoringInstrumentation monitoringInstrumentation) {
		Util.monitoringInstrumentation = monitoringInstrumentation;
	}
	
	public static MonitoringInstrumentation getMonitoringInstrumentation() {
		if (monitoringInstrumentation == null) {
			synchronized(Util.class) {
				if (monitoringInstrumentation == null) {
					try {
						String configFilePath = System.getProperty("rpMonitoringInstrumentationConfigFilePath");
						System.out.println("Config File Path:" + configFilePath);
						
						JAXBContext context = JAXBContext.newInstance(MonitoringInstrumentation.class);
						Unmarshaller um = context.createUnmarshaller();
						monitoringInstrumentation = (MonitoringInstrumentation) um.unmarshal(new FileReader(configFilePath));
					    
						System.out.println("MonitoringInstrumentation config file successfully loaded");
						//dumpMonitoringInstrumentation(monitoringInstrumentation);
					}
					catch (Exception ex) {
						monitoringInstrumentation = null;
						System.out.println("MonitoringInstrumentation config file did not load successfully");
					}
				}
			}
		}
		
		return monitoringInstrumentation;
	}
	
	// This method verifies that each String member of checks exists in values
	public static boolean containsAtLeastOneString(String[] values, String[] checks) {
		boolean contains = false;
		
		if (checks != null) {
			for (String ch : checks) {
				
				for (String value : values) {
					if (value.equals(ch)) {
						contains = true;
						break;
					}
				}
				
				if (contains == true) {
					break;
				}
			}
		}
		
		return contains;
	}
	
	// This method verifies that each String member of checks exists in values
	public static boolean containsAllStrings(String[] values, String[] checks) {
		boolean contains = true;
		
		if (checks != null) {
			for (String ch : checks) {
				
				boolean containsCheck = false;
				
				for (String value : values) {
					if (value.equals(ch)) {
						containsCheck = true;
					}
				}
				
				if (containsCheck == false) {
					contains = false;
					break;
				}
			}
		}
		
		return contains;
	}
	
	public static boolean methodAnnotationMatches(Class<?> clazz, Method method) {
		boolean matches = false;
		
		MonitoringInstrumentation monitoringInstrumentation = getMonitoringInstrumentation();
		List<MonitorCategory> monitorCategories = monitoringInstrumentation.getMonitorCategories();

		List<String> monitorCategoriesList = new ArrayList<String>();
		for (MonitorCategory monitorCategory : monitorCategories) {
			if (monitorCategory.isActive() == true) {
				monitorCategoriesList.add(monitorCategory.getCategoryName());
			}
		}
		
		String categories[] = monitorCategoriesList.toArray(new String[monitorCategoriesList.size()]);
		
		Annotation[] methodAnnotations= method.getAnnotations();
				
		if (methodAnnotations != null) {
			for (Annotation annotation : methodAnnotations) {
				if (annotation.annotationType() == MethodLogger.class) {
					MethodLogger methodLogger = (MethodLogger) annotation;
					String[] values = methodLogger.value();
										
					matches = Util.containsAtLeastOneString(values,  categories);
									
					break;
				}
			}
		}
		
		return matches;
	}
	
	private static boolean regexMatches(String pattern, String stringToMatch) {
		String regexPattern = wildcardToRegex(pattern);
		
		boolean matches = false;
		
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(stringToMatch);
		if (m.matches()) {
			matches = true;
		}
		
		return matches;
		//return stringToMatch.equals(pattern);
	}
		
	public static boolean isEligibleClass(String className) {
		boolean eligible = false;
						
		if (className.contains("com.realpage.monitor.instrumentation") == false) {
						
			MonitoringInstrumentation monitoringInstrumentation = getMonitoringInstrumentation();
			Eligibles eligibles = monitoringInstrumentation.getEligibles();
			List<EligibleClass> eligibleClasses = eligibles.getEligibleClasses();
					
			for (EligibleClass eligibleClass : eligibleClasses) {
				if (regexMatches(eligibleClass.getClassName(), className)) {
					if (eligibleClass.isActive() == true) {
						eligible = true;
					}
				}
			}
		}
		
		return eligible;
	}
	
	public static boolean isEligibleMethod(String className, String methodName) {
		boolean eligible = false;

		MonitoringInstrumentation monitoringInstrumentation = getMonitoringInstrumentation();
		Eligibles eligibles = monitoringInstrumentation.getEligibles();
		List<EligibleClass> eligibleClasses = eligibles.getEligibleClasses();
				
		for (EligibleClass eligibleClass : eligibleClasses) {
			if (regexMatches(eligibleClass.getClassName(), className)) {
				if (eligibleClass.isActive() == true) {
					List<EligibleMethod> eligibleMethods = eligibleClass.getEligibleMethods();
					
					for (EligibleMethod eligibleMethod : eligibleMethods) {
						if (regexMatches(eligibleMethod.getMethodName(), methodName)) {
							if (eligibleMethod.isActive() == true) {
								eligible = true;
							}
						}
					}
				}
				
			}
		}
		
		return eligible;
	}
	
	public static String[] getMethodCategories(String className, String methodName) {
		
		String[] categories = null;
		
		MonitoringInstrumentation monitoringInstrumentation = getMonitoringInstrumentation();
		Eligibles eligibles = monitoringInstrumentation.getEligibles();
		List<EligibleClass> eligibleClasses = eligibles.getEligibleClasses();
		
		List<String> categoryNames = new ArrayList<String>();
		
		for (EligibleClass eligibleClass : eligibleClasses) {
			if (regexMatches(eligibleClass.getClassName(), className)) {
				if (eligibleClass.isActive() == true) {
					List<EligibleMethod> eligibleMethods = eligibleClass.getEligibleMethods();
					
					for (EligibleMethod eligibleMethod : eligibleMethods) {
						if (regexMatches(eligibleMethod.getMethodName(), methodName)) {
							if (eligibleMethod.isActive() == true) {
								List<MethodCategory> methodCategories = eligibleMethod.getMethodCategories();
								for (MethodCategory methodCategory : methodCategories) {
									if (methodCategory.isActive()) {
										System.out.println("Adding method category: " + methodCategory.getCategoryName());
										categoryNames.add(methodCategory.getCategoryName());
									}
								}
							}
						}
					}
				}
				
			}
		}
				
		if (categoryNames != null) {
			categories = categoryNames.toArray(new String[categoryNames.size()]);
		}
		
		return categories;
	}
	
	@SuppressWarnings("unused")
	private static void dumpMonitoringInstrumentation(MonitoringInstrumentation monitoringInstrumentation) {
		Eligibles eligibles = monitoringInstrumentation.getEligibles();
		List<EligibleClass> eligibleClasses = eligibles.getEligibleClasses();
		List<MonitorCategory> monitorCategories = monitoringInstrumentation.getMonitorCategories();
		List<MonitorDirective> monitorDirectives = monitoringInstrumentation.getMonitorDirectives();
		
		for (MonitorDirective monitorDirective : monitorDirectives) {
			System.out.println(" monitorDirective:" + monitorDirective.getDirectiveName() + "=" + monitorDirective.getDirectiveValue());
		}
		
		for (MonitorCategory monitorCategory : monitorCategories) {
			System.out.println(" monitorCategory:" + monitorCategory.getCategoryName());
		}
		
		for (EligibleClass eligibleClass : eligibleClasses) {
			System.out.println(" eligibleClass:" + eligibleClass.getClassName());
			
			List<EligibleMethod> eligibleMethods = eligibleClass.getEligibleMethods();
			for (EligibleMethod eligibleMethod : eligibleMethods) {
				System.out.println("   eligibleMethod:" + eligibleMethod.getMethodName());
				
				List<MethodCategory> categories = eligibleMethod.getMethodCategories();
				for (MethodCategory category : categories) {
					System.out.println("     category:" + category.getCategoryName());
				}
			}
		}

	}
	
	private static String wildcardToRegex(String wildcard) {
		if (wildcard == null)
			return null;

		StringBuffer buffer = new StringBuffer();

		char[] chars = wildcard.toCharArray();

		for (int i = 0; i < chars.length; ++i) {
			if (chars[i] == '*')
				buffer.append(".*");
			else if (chars[i] == '?')
				buffer.append(".");
			else if ("+()^$.{}[]|\\".indexOf(chars[i]) != -1)
				buffer.append('\\').append(chars[i]); // prefix all metacharacters with // backslash
			else
				buffer.append(chars[i]);

		}

		return buffer.toString();
	}
	
	public static String getMonitorDirectiveValue(String monitorDirectiveName, String defaultValue) {
		String monitorDirectiveValue = defaultValue;
		
		MonitoringInstrumentation monitoringInstrumentation = getMonitoringInstrumentation();
		
		if (monitoringInstrumentation != null) {
			List<MonitorDirective> monitorDirectives = monitoringInstrumentation.getMonitorDirectives();
			
			for (MonitorDirective monitorDirective : monitorDirectives) {
				if (monitorDirective.getDirectiveName().equals(monitorDirectiveName)) {
					monitorDirectiveValue = monitorDirective.getDirectiveValue();
					break;
				}
			}
		}
		
		return defaultValue;
	}
	
	public static boolean isOutputResults(Class<?> clazz, String methodName) {
		boolean outputResults = false;
		
		String directiveOutputResults = Util.getMonitorDirectiveValue("outputResults", "true");
	
		outputResults = Boolean.parseBoolean(directiveOutputResults);
		
		return outputResults;
	}
	
	public static String generateCorrelationID() {
		String correlationID = "";
		
		String correlationIDGeneration = getMonitorDirectiveValue("correlationIDGeneration", "UUID");
		
		if (correlationIDGeneration.equals("UUID")) {
			UUID uuid = UUID.randomUUID();
			
			correlationID = uuid.toString();
		}
		
		return correlationID;
	}
	
	static {
		watchConfigFile();
	}
}
