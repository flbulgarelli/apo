package com.uqbar.aop.javassit.parser;

import javassist.expr.FieldAccess;

public class JavassitParser {
	
	public static String parser(String string){
		return parser(null, string);
	}
	
	public static String parser(FieldAccess fieldAccess, String string){
		String result = string;
		for (Tokens tokens : Tokens.values()) {
			if(result.contains(tokens.getRegExp())){
				result = result.replace(tokens.getRegExp(), tokens.getJavassistExp(fieldAccess));
			}
		}
		
		return result;
	}
	
}
