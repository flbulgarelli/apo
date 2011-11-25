package com.uqbar.renascent.framework.aop;

import java.util.HashMap;
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
import com.uqbar.renascent.framework.aop.DirtyFlagFieldAccessInterceptor;
import com.uqbar.renascent.framework.aop.GenericObjectReferenceFieldAccessInterceptor;
import com.uqbar.renascent.framework.aop.IAdviceWeaver;
import com.uqbar.renascent.framework.aop.LazyRelationResolverFieldAccessInterceptor;

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados. Solo aspectea los
 * objectos com.uqbar.renascent.framework.persistence.Persistible!!!!!!!
 * 
 * @author nny
 * @author rgomez
 * @author lgassman
 * @author jfernandes
 */
public class AdviceWeaver implements IAdviceWeaver {
	public static final String CLASSNAME_PERSISTIBLE = "com.uqbar.renascent.framework.persistence.Persistible";
	/** Map with "joinpont" -> "advice" */
	private Map<Predicate<CtClass>, ExprEditor> weavingInstrumentors = new HashMap<Predicate<CtClass>, ExprEditor>();
	private final IBehaviorAdviceWeaverStrategy strategy;

	public AdviceWeaver(ClassPool classPool, IBehaviorAdviceWeaverStrategy strategy) {
		this.strategy = strategy;
		this.addInstrumentors(classPool);
	}

	protected void addInstrumentors(ClassPool classPool) {
		strategy.addInstrumentors(classPool, weavingInstrumentors);
		
		this.getWeavingInstrumentors().put(new Predicate<CtClass>() {
			public boolean evaluate(CtClass ctClass) {
				return ctClass.getName().equals(
					"com.uqbar.renascent.framework.domain.GenericObjectReference");
			}
		}, new WeavingInstrumentor().addFieldAccessInterceptor(new GenericObjectReferenceFieldAccessInterceptor()));
	}

	public void applyAdvice(CtClass ctClass) throws CannotCompileException {
		if (!ctClass.isFrozen()) {
			for (Entry<Predicate<CtClass>, ExprEditor> entry : this.getWeavingInstrumentors().entrySet()) {
				if (entry.getKey().evaluate(ctClass)) {
					applyAdviceToCtClass(ctClass, entry);
					return;
				}
			}
		}
	}

	protected void applyAdviceToCtClass(CtClass ctClass,
			Entry<Predicate<CtClass>, ExprEditor> entry)
			throws CannotCompileException {
		strategy.applyAdviceToCtClass(ctClass, entry);
		ctClass.instrument(entry.getValue());
	}

	protected Map<Predicate<CtClass>, ExprEditor> getWeavingInstrumentors() {
		return weavingInstrumentors;
	}
	
}