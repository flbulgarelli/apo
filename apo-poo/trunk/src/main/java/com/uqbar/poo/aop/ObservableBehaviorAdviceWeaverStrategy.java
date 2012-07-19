package com.uqbar.poo.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import org.uqbar.commons.utils.Observable;

import com.uqbar.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.aop.WeavingInstrumentor;
import com.uqbar.aop.pointcut.predicate.APredicate;
import com.uqbar.aop.pointcut.predicate.HasAnnotationPredicate;

public class ObservableBehaviorAdviceWeaverStrategy implements IBehaviorAdviceWeaverStrategy {
	
	private ObservableFieldAccessInterceptor observableFieldAccessInterceptor = new ObservableFieldAccessInterceptor();
	private ObservableBehavior observableBehavior = new ObservableBehavior();

	
	@Override
	public void addInstrumentors(ClassPool classPool, Map<APredicate, ExprEditor> weavingInstrumentors) {
		weavingInstrumentors.put(
			new HasAnnotationPredicate(classPool, Observable.class.getName()),
			new WeavingInstrumentor()
					.addFieldAccessInterceptor(observableFieldAccessInterceptor));

	}
	
	@Override
	public void applyAdviceToCtClass(CtClass ctClass, Entry<APredicate, ExprEditor> entry)	{
		observableBehavior.addBehavior(ctClass);
	}

	@Override
	public void configureInstrumentor(WeavingInstrumentor instrumentor) {
		instrumentor.addFieldAccessInterceptor(observableFieldAccessInterceptor);
	}	

}
