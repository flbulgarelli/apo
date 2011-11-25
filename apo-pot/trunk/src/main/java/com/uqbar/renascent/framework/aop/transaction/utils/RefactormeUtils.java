package com.uqbar.renascent.framework.aop.transaction.utils;

import java.lang.reflect.Field;

/**
 * Holds util methods related to reflection and low level code, that should
 * be moved up to more generic projects like commons, commons-lang etc.
 * 
 * @author jfernandes
 */
public class RefactormeUtils {

    public static Field seekField(Class<?> keyClass, String fieldName) throws NoSuchFieldException {
	try {
	    return keyClass.getDeclaredField(fieldName);
	}
	catch (NoSuchFieldException noSuchFieldException) {
	    keyClass = keyClass.getSuperclass();
	    if (keyClass != null) {
		return seekField(keyClass, fieldName);
	    }
	    throw noSuchFieldException;
	}
    }
    
    public static void forceFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
	Field field = seekField(object.getClass(), fieldName);
	field.setAccessible(true);
	field.set(object, value);
    }
    
}
