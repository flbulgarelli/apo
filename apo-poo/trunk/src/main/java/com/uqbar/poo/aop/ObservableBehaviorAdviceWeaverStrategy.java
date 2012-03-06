package com.uqbar.poo.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import org.uqbar.commons.utils.Observable;

import com.uqbar.commons.collections.Predicate;
import com.uqbar.renascent.aop.pointcut.predicate.APredicate;
import com.uqbar.renascent.aop.pointcut.predicate.HasAnnotationPredicate;
import com.uqbar.renascent.aop.pointcut.predicate.OrPredicate;
import com.uqbar.renascent.framework.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.renascent.framework.aop.WeavingInstrumentor;

public class ObservableBehaviorAdviceWeaverStrategy implements IBehaviorAdviceWeaverStrategy {
	
	private ObservableFieldAccessInterceptor observableFieldAccessInterceptor = new ObservableFieldAccessInterceptor();
	private ObservableBehavior observableBehavior = new ObservableBehavior();

	
	@Override
	public void addInstrumentors(ClassPool classPool, Map<APredicate, ExprEditor> weavingInstrumentors) {
		// persistence - collections
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
