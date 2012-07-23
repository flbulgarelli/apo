package com.uqbar.pot.aop;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;

import org.uqbar.commons.utils.Transactional;

import com.uqbar.aop.Advice;
import com.uqbar.aop.BehaviorAdviceWeaverStrategy;
import com.uqbar.aop.WeavingInstrumentor;
import com.uqbar.aop.pointcut.predicate.HasAnnotationPredicate;

/**
 * @author nny
 *
 */
public class TransactionalBehaviorAdviceWeaverStrategy implements BehaviorAdviceWeaverStrategy {
	
	private TransactionFieldAccessInterceptor transactionInterceptor = new TransactionFieldAccessInterceptor();

	@Override
	public void addAdvices(ClassPool classPool, List<Advice> advices) {
		// transactions
		advices.add( new Advice(
			new HasAnnotationPredicate(classPool, Transactional.class.getName()),
			new WeavingInstrumentor().addFieldAccessInterceptor(transactionInterceptor)));
	}

	@Override
	public void applyAdviceToCtClass(CtClass ctClass, Advice advice) {
	}

	@Override
	public void configureInstrumentor(WeavingInstrumentor instrumentor) {
		instrumentor.addFieldAccessInterceptor(transactionInterceptor);
	}

}
