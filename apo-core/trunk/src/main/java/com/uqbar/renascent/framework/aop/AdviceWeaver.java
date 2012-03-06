package com.uqbar.renascent.framework.aop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;

import com.uqbar.commons.collections.Predicate;
import com.uqbar.renascent.aop.pointcut.predicate.APredicate;

/**
 * Colabora con el FrameworkClassLoader para construir los Aspectos que fueron declarados.
 * 
 * @author nny
 * @author rgomez
 * @author lgassman
 * @author jfernandes
 */
public class AdviceWeaver implements IAdviceWeaver {
	/** Map with "joinpont" -> "advice" */
	private Map<APredicate, ExprEditor> weavingInstrumentors = new HashMap<APredicate, ExprEditor>();
	private final IBehaviorAdviceWeaverStrategy strategy;

	public AdviceWeaver(ClassPool classPool, IBehaviorAdviceWeaverStrategy strategy) {
		this.strategy = strategy;
		this.addInstrumentors(classPool);
	}

	protected void addInstrumentors(final ClassPool classPool) {
		strategy.addInstrumentors(classPool, weavingInstrumentors);
	}

	public void applyAdvice(CtClass ctClass) throws CannotCompileException, NotFoundException {
		if (!ctClass.isFrozen()) {
			for (Entry<APredicate, ExprEditor> entry : this.getWeavingInstrumentors().entrySet()) {
				if (entry.getKey().evaluate(ctClass)) {
					applyAdviceToCtClass(ctClass, entry);
				}
				
				for (CtField ctField : ctClass.getDeclaredFields()) {
					if (entry.getKey().evaluate(ctField)) {
						applyAdviceToCtClass(ctField.getType(), entry);
					}	
				}
			}
		}
	}

	protected void applyAdviceToCtClass(CtClass ctClass,
			Entry<APredicate, ExprEditor> entry)
			throws CannotCompileException {
		strategy.applyAdviceToCtClass(ctClass, entry);
		ctClass.instrument(entry.getValue());
	}

	protected Map<APredicate, ExprEditor> getWeavingInstrumentors() {
		return weavingInstrumentors;
	}
	
}