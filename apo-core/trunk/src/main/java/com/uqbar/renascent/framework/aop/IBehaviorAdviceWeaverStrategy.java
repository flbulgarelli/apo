package com.uqbar.renascent.framework.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import com.uqbar.renascent.aop.pointcut.predicate.APredicate;

public interface IBehaviorAdviceWeaverStrategy {

	void applyAdviceToCtClass(CtClass ctClass, Entry<APredicate, ExprEditor> entry)
			throws CannotCompileException;

	void addInstrumentors(ClassPool classPool,
			Map<APredicate, ExprEditor> weavingInstrumentors);
	
	void configureInstrumentor(WeavingInstrumentor instrumentor);

}