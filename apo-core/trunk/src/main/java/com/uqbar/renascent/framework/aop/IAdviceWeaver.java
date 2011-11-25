package com.uqbar.renascent.framework.aop;

import javassist.CannotCompileException;
import javassist.CtClass;

public interface IAdviceWeaver {

	public abstract void applyAdvice(CtClass ctClass)
			throws CannotCompileException;

}