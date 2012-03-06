package com.uqbar.renascent.framework.aop;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public interface IAdviceWeaver {

	public abstract void applyAdvice(CtClass ctClass)
			throws CannotCompileException, NotFoundException;

}