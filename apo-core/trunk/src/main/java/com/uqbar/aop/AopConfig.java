package com.uqbar.aop;

import java.io.InputStream;
import java.util.Properties;

/**
 * DOCME
 */
public abstract class AopConfig {
	private static final String PROPERTIES_FILE = "framework-config.properties";
	private static final String FILE_PREFIX = "framework.aop";
	private static final String FILE_SUFFIX = "enable";
	private static final String AOP_ENABLE_KEY = "classloader";

	private static Properties properties;

	private static synchronized Properties getProperties() {
		if (AopConfig.properties == null) {
			try {
				AopConfig.properties = new Properties();
				InputStream file = AopConfig.class.getClassLoader()
						.getResourceAsStream(PROPERTIES_FILE);
				AopConfig.properties.load(file);
				file.close();
			} 
			catch (Exception ex) {
				throw new RuntimeException("Error while loading configuration file!", ex);
			}
		}
		return AopConfig.properties;
	}

	/**
	 * Este m�todo es usado para pedir la habilitaci�n con toda la key: fullKey
	 * = prefix + key + suffix
	 */
	private static Boolean isEnableWithFullKey(String fullKey) {
		return Boolean.valueOf(AopConfig.getProperties().getProperty(fullKey, "false"));
	}

	/**
	 * pregunta si esta habilitada la key En el archivo de properties, debe
	 * figurar: prefix + key + suffix
	 */
	public static Boolean isEnable(String shortKey) {
		return isEnableWithFullKey(FILE_PREFIX + "." + shortKey + "." + FILE_SUFFIX);
	}
	
	public static String getProperty(String propertyName) {
		return AopConfig.getProperties().getProperty(propertyName);
	}

	public static Boolean isAOPEnable() {
		return isEnable(AOP_ENABLE_KEY);
	}

	/**
	 * A prueba de tontos, para que la optimizacion dirty este habilitada
	 * tambien debe estar el aspecteo que manipula el dirty flag (de lo
	 * contrario, no se grabaria ningun objeto nunca!)
	 * 
	 * @return true si la optimizacion, que consiste en no grabar un objeto que
	 *         no este dirty, esta deshablitada.
	 */
	public static boolean isDirtyOptimizationDisabled() {
		return !(isEnable("DirtyFlagFieldAccessInterceptor").booleanValue()
				&& isEnable("DirtyUpdateOptimization").booleanValue() && isAOPEnable()
				.booleanValue());
	}

}
