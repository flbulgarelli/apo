package com.uqbar.pot.aop;

import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;

import org.uqbar.commons.utils.Transactional;

import com.uqbar.renascent.aop.pointcut.predicate.APredicate;
import com.uqbar.renascent.aop.pointcut.predicate.HasAnnotationPredicate;
import com.uqbar.renascent.framework.aop.IBehaviorAdviceWeaverStrategy;
import com.uqbar.renascent.framework.aop.WeavingInstrumentor;

/**
 * Ver como interceptar los mensajes add: y remove: de las Collections.
 * Caso mas sencillo es clonar las collections, y trabajar con la clonada hasta hacer el commit
 * 
 * Ver DynamicProxy
 * 
 * [DOC] Trabajo fururo en una estrategia que vea que es implementacion es mas inteligente.
 * @author nny
 *
 */
public class TransactionalBehaviorAdviceWeaverStrategy implements
		IBehaviorAdviceWeaverStrategy {
	
	private TransactionFieldAccessInterceptor transactionInterceptor = new TransactionFieldAccessInterceptor();


	@Override
	public void addInstrumentors(ClassPool classPool,
			Map<APredicate, ExprEditor> weavingInstrumentors) {
		
		// transactions
		weavingInstrumentors.put(
			new HasAnnotationPredicate(classPool, Transactional.class.getName()),
			new WeavingInstrumentor()
					.addFieldAccessInterceptor(transactionInterceptor));

	}

	@Override
	public void applyAdviceToCtClass(CtClass ctClass,
			Entry<APredicate, ExprEditor> entry)
			throws CannotCompileException {

	}

	@Override
	public void configureInstrumentor(WeavingInstrumentor instrumentor) {
		instrumentor.addFieldAccessInterceptor(transactionInterceptor);
	}

}
