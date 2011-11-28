package com.uqbar.pot.aop;

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
import com.uqbar.renascent.framework.aop.AdviceWeaver;
import com.uqbar.renascent.framework.aop.DirtyFlagFieldAccessInterceptor;
import com.uqbar.renascent.framework.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.renascent.framework.aop.LazyRelationResolverFieldAccessInterceptor;
import com.uqbar.renascent.framework.aop.WeavingInstrumentor;

public class TransactionalBehaviorAdviceWeaverStrategy implements
		IBehaviorAdviceWeaverStrategy {
	
	private TransactionFieldAccessInterceptor transactionInterceptor = new TransactionFieldAccessInterceptor();


	@SuppressWarnings("unchecked")
	@Override
	public void addInstrumentors(ClassPool classPool,
			Map<Predicate<CtClass>, ExprEditor> weavingInstrumentors) {
		weavingInstrumentors.put(new SuperClassPredicate(classPool, AdviceWeaver.CLASSNAME_PERSISTIBLE),
				new WeavingInstrumentor()
					//transactions
					.addFieldAccessInterceptor(transactionInterceptor));
					// persistence
		
		// transactions - collections
		weavingInstrumentors.put(new OrPredicate<CtClass>(
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.Itr"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.AbstractCollection"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.SubListListIterator"),
			new SuperClassPredicate(classPool, "com.uqbar.renascent.common.collections.BasicList"),
			new HasAnnotationPredicate(classPool, "org.uqbar.commons.utils.Transactional")),
			new WeavingInstrumentor()
					.addFieldAccessInterceptor(transactionInterceptor));

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
