/*
 * Created on 16-jul-2004 
 */
package com.uqbar.renascent.common.text;

import java.util.Locale;

/**
 * Solo agrega metodos estaticos sobrecargas de format(), para no tener que crear el array de argumentos de afuera.
 * 
 * @author jfernandes 
 */
public class MessageFormat extends java.text.MessageFormat {

	// *********************************
	// ** Constructores
	// *********************************
	
	public MessageFormat(String pattern) {
		super(pattern);
    }
	 
    public MessageFormat(String pattern, Locale locale) {
        super(pattern, locale);
    }

	// *********************************
	// ** Metodos
	// *********************************
    
    public static String format(String pattern, Object argument) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(new Object[] {argument});
    }
    
    public static String format(String pattern, Object arg1, Object arg2) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(new Object[] {arg1, arg2});
    }
    
    public static String format(String pattern, Object arg1, Object arg2, Object arg3) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(new Object[] {arg1, arg2, arg3});
    }
    
    public static String format(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(new Object[] {arg1, arg2, arg3, arg4});
    }
    
}
