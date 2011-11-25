package com.uqbar.poo.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import com.uqbar.commons.collections.Predicate;
import com.uqbar.renascent.aop.pointcut.predicate.HasAnnotationPredicate;
import com.uqbar.renascent.aop.pointcut.predicate.OrPredicate;
import com.uqbar.renascent.aop.pointcut.predicate.SuperClassPredicate;
import com.uqbar.renascent.framework.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.renascent.framework.aop.WeavingInstrumentor;

public class ObservableBehaviorAdviceWeaverStrategy implements IBehaviorAdviceWeaverStrategy {
	
	private ObservableFieldAccessInterceptor observableFieldAccessInterceptor = new ObservableFieldAccessInterceptor();
	private ObservableBehavior observableBehavior = new ObservableBehavior();

	
	@SuppressWarnings("unchecked")
	@Override
	public void addInstrumentors(ClassPool classPool, Map<Predicate<CtClass>, ExprEditor> weavingInstrumentors) {
		// persistence - collections
		weavingInstrumentors.put(new OrPredicate<CtClass>(
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.Itr"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.AbstractCollection"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.SubListListIterator"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.BasicList"),
			new HasAnnotationPredicate(classPool, "org.uqbar.commons.utils.Transactional")),
			new WeavingInstrumentor()
					.addFieldAccessInterceptor(observableFieldAccessInterceptor));

	}
	
	@Override
	public void applyAdviceToCtClass(CtClass ctClass, Entry<Predicate<CtClass>, ExprEditor> entry)	{
		observableBehavior.addBehavior(ctClass);
	}

	@Override
	public void configureInstrumentor(WeavingInstrumentor instrumentor) {
		instrumentor.addFieldAccessInterceptor(observableFieldAccessInterceptor);
	}	

}
