package com.uqbar.pot.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import org.uqbar.commons.utils.Transactional;

import com.uqbar.commons.collections.Predicate;
import com.uqbar.renascent.aop.pointcut.predicate.HasAnnotationPredicate;
import com.uqbar.renascent.aop.pointcut.predicate.OrPredicate;
import com.uqbar.renascent.aop.pointcut.predicate.SuperClassPredicate;
import com.uqbar.renascent.framework.aop.AdviceWeaver;
import com.uqbar.renascent.framework.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.renascent.framework.aop.WeavingInstrumentor;

public class TransactionalBehaviorAdviceWeaverStrategy implements
		IBehaviorAdviceWeaverStrategy {
	
	private TransactionFieldAccessInterceptor transactionInterceptor = new TransactionFieldAccessInterceptor();


	@Override
	public void addInstrumentors(ClassPool classPool,
			Map<Predicate<CtClass>, ExprEditor> weavingInstrumentors) {
		
		// transactions - collections
		weavingInstrumentors.put(
			new HasAnnotationPredicate(classPool, Transactional.class.getName()),
			new WeavingInstrumentor()
					.addFieldAccessInterceptor(transactionInterceptor));

//		new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.Itr" ),
//		new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.AbstractCollection"),
//		new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.SubListListIterator"),
//		new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.BasicList"),
	}

	@Override
	public void applyAdviceToCtClass(CtClass ctClass,
			Entry<Predicate<CtClass>, ExprEditor> entry)
			throws CannotCompileException {

	}

	@Override
	public void configureInstrumentor(WeavingInstrumentor instrumentor) {
		instrumentor.addFieldAccessInterceptor(transactionInterceptor);
	}

}
