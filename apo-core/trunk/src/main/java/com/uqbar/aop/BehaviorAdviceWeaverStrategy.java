package com.uqbar.aop;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;

public interface BehaviorAdviceWeaverStrategy {

	void applyAdviceToCtClass(CtClass ctClass, Advice entry);

	void addAdvices(ClassPool classPool, List<Advice> weavingInstrumentors);
	
	void configureInstrumentor(WeavingInstrumentor instrumentor);

}