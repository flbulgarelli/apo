package com.uqbar.renascent.framework.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import com.uqbar.commons.collections.Predicate;

public interface IBehaviorAdviceWeaverStrategy {

	void applyAdviceToCtClass(CtClass ctClass, Entry<Predicate<CtClass>, ExprEditor> entry)
			throws CannotCompileException;

	void addInstrumentors(ClassPool classPool,
			Map<Predicate<CtClass>, ExprEditor> weavingInstrumentors);
	
	void configureInstrumentor(WeavingInstrumentor instrumentor);

}