package com.uqbar.renascent.aop.pointcut.predicate;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import com.uqbar.commons.collections.Predicate;

/**
 * Predicate que recibe un CtClass y evalua si la esa clase es hija en cualquier nivel de la clase con la
 * cual se construye
 * 
 * @author lgassman
 */
public class SuperClassPredicate implements Predicate<CtClass> {
	private CtClass superClass;
	private String superClassName;
	private ClassPool classPool;

	public SuperClassPredicate(ClassPool classPool, String superClassName) {
		this.superClassName = superClassName;
		this.classPool = classPool;
	}

	public boolean evaluate(CtClass ctClass) {
		if (ctClass == null) {
			return false;
		}
		try {
			if (this.superClass == null) {
				this.superClass = this.classPool.get(this.superClassName);
			}
			return ((CtClass) ctClass).subtypeOf(this.superClass);
		}
		catch (NotFoundException exception) {
			return false;
		}
		catch (RuntimeException exception) {
			try {
				System.err.println("No se pudo aspectear la clase " + ctClass + ". Causa: " + exception);
			}
			catch (java.lang.RuntimeException e) {
				System.err.println("#####ERROR#####\n#####ERROR#####\nNo se pudo aspectear ni mostrar la causa" + e);
			}
			return false;
		}
	}
}