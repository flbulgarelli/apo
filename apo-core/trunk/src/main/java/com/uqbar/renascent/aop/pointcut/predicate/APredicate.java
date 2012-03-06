package com.uqbar.renascent.aop.pointcut.predicate;

import javassist.CtClass;
import javassist.CtField;

public interface APredicate {
	
	public boolean evaluate(CtClass ctClass);
	
	public boolean evaluate(CtField ctField);

}
