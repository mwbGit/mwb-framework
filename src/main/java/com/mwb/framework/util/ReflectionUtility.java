package com.mwb.framework.util;

import com.mwb.framework.log.Log;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ReflectionUtility {
	private final static Log LOG = Log.getLog(ReflectionUtility.class);

	public static Set<Class<?>> getClassesInPackage(String packageName) throws Exception {  
		Set<Class<?>> classes = new HashSet<Class<?>>(); 

		String packageNameSlashed = packageName.replace(".", "/");

		// Get a File object for the package  
		URL directoryURL = Thread.currentThread().getContextClassLoader().getResource(packageNameSlashed);
		
		//URL directoryURL = new URL("file:/D:/DocuMent/Development/Source/git/mwb-platform/x2/webapp/admin/target/mwb-platform-x2-webapp-admin-1.0/WEB-INF/lib/mwb-platform-data-dao-1.0.jar!/com/mwb/platform/data/dao/mapper");
		if (directoryURL == null) {  
			String error = String.format("Could not retrieve URL resource: %s", packageNameSlashed);

			LOG.error(error);  

			throw new RuntimeException(error);
		}  
		
		String directoryString = directoryURL.getFile();  
		if (directoryString == null) { 
			String error = String.format("Could not find directory for URL resource: %s", packageNameSlashed);

			LOG.error(error);  

			throw new RuntimeException(error);
		}  

		if (isInJar(directoryString)) { 
			// 如果package在一个jar文件里面
			String jarName = getJarName(directoryString);
			
			JarInputStream jarFile = null;
			
			try {
				jarFile = new JarInputStream(new URL(jarName).openStream());
				
				while(true) {
					JarEntry jarEntry = jarFile.getNextJarEntry ();
					
					if(jarEntry == null) {
						break;
					}
					
					if((jarEntry.getName().startsWith(packageNameSlashed)) && (jarEntry.getName().endsWith (".class"))) {
						String className = jarEntry.getName().replaceAll("/", "\\.");
						classes.add(Class.forName(className.substring(0, className.length() - 6)));
					}
				}
			} finally {
				if (jarFile != null) {
					jarFile.close();
				}
			}
			
		} else {
			// 如果package在文件系统目录下面
			File directory = new File(directoryString);  
			if (directory.exists()) {  
				// Get the list of the files contained in the package  
				String[] files = directory.list();  
				
				for (String fileName : files) {  
					// We are only interested in .class files  
					
					if (fileName.endsWith(".class")) {  
						// Remove the .class extension  
						fileName = fileName.substring(0, fileName.length() - 6);
						classes.add(Class.forName(packageName + "." + fileName));
					}
				}  
			} else {  
				String error = String.format("%s does not appear to exist as a valid package on the file system.", packageName);

				LOG.error(error);  

				throw new RuntimeException(error);
			}  
		}
		
		return classes;  
	} 
	
	public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        
        return fields;
    }
	
	public static Field getFromAllField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
        	try {
        		return c.getDeclaredField(name);
        	} catch (Exception e) {
        		
        	}
        }
        
        return null;
    }
	
	// fieldFullName例子：store.id
	public static Object getFieldValueByPath(Object object, String fieldFullName) {
		try {
			String[] fieldNames = fieldFullName.split("\\.");
			
			Object currentObj = object;
			for (String fieldName : fieldNames) {
				Class<?> clazz = currentObj.getClass();
				
				Field field = getFromAllField(clazz, fieldName);
				if (field == null) {
					return null;
				}

				field.setAccessible(true);

				currentObj = field.get(currentObj);
			}
			
			return currentObj;
		} catch (Exception e) {
			LOG.error("Failed to get field value from path " + fieldFullName, e);
		}
		
		return null;
	}
	
	// 获取接口上定义的annotation
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Annotation getMethodAnnotationFromInterface(Method method, Class annotationClass) {
		
		Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
		for (Class<?> interfaze : interfaces) {
			try {
				Method overridedMethod = interfaze.getMethod(method.getName(), method.getParameterTypes());
				
				return overridedMethod.getAnnotation(annotationClass);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
	
	// 获取接口上定义的annotation
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Annotation getAnnotationFromInterface(Class clazz, Class annotationClass) {
		
		if (clazz.isInterface()) {
			Annotation annotation  = clazz.getAnnotation(annotationClass);
			
			if (annotation != null) {
				return annotation;
			} 
		}
		
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interfaze : interfaces) {
			try {
				Annotation annotation  = interfaze.getAnnotation(annotationClass);
				
				if (annotation == null) {
					continue;
				} 
				
				return annotation;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
	
	// 通过递归获取接口上定义的annotation
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Annotation getMethodAnnotationFromInterfaceByRecursion(Class clazz, Method method, Class annotationClass) {
		
		Annotation annotation = null;
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interfaze : interfaces) {
			try {
				Method overridedMethod = interfaze.getMethod(method.getName(), method.getParameterTypes());
				
				annotation = overridedMethod.getAnnotation(annotationClass);
				if (annotation != null) {
					return annotation;
				}
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
		
		Class superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getMethodAnnotationFromInterfaceByRecursion(superClass, method, annotationClass);
		}
		return null;
	}
		
	// 通过递归获取interface上的接口
	@SuppressWarnings("rawtypes")
	public static Annotation getAnnotationFromInterfaceByRecursion(Class clazz, Class annotationClass) {
		
		Annotation annotation = getAnnotationFromInterface(clazz, annotationClass);
		if (annotation != null) {
			return annotation;
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getAnnotationFromInterfaceByRecursion(superClass, annotationClass);
		}
		
		return null;
	}
	
	public static Annotation[][] getOverridedParameterAnnotations(Method method) {
		Annotation[][] parameterAnnotations = new Annotation[0][0];
		
		Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
		for (Class<?> interfaze : interfaces) {
			try {
				Method overridedMethod = interfaze.getMethod(method.getName(), method.getParameterTypes());
				
				return overridedMethod.getParameterAnnotations();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return parameterAnnotations;
	}
	
	private static boolean isInJar(String path) {
		if (path.contains(".jar!")) {
			return true;
		} else {
			return false;
		}
	}
	
	private static String getJarName(String path) {
		String[] tmp = path.split("\\.jar\\!");
		
		return tmp[0] + ".jar";
	}
	
	public static void main(String[] args) throws Exception {
		ReflectionUtility.getClassesInPackage("com.mwb.delivery.data.dao.mapper");
	}
}
