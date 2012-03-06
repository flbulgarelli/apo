package com.uqbar.renascent.aop.pointcut.predicate;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.NotFoundException;

import com.uqbar.commons.collections.Predicate;
import com.uqbar.commons.exceptions.ProgramException;

/**
 * Evaluates whether the given CtClass has been annotated with a given annotation class.
 * 
 * @author jfernandes
 */
public class HasAnnotationPredicate implements APredicate {
	private CtClass annotationType;
	private String annotationTypeName;
	private ClassPool classPool;

	public HasAnnotationPredicate(ClassPool classPool, String annotationTypeName) {
		this.classPool = classPool;
		this.annotationTypeName = annotationTypeName;
	}

	@Override
	public boolean evaluate(CtClass clazz) {
		try {
			for (Object annotationProxy : clazz.getAnnotations()) {
				if (implementsAnnotationInterface(annotationProxy.getClass().getInterfaces())) {
					return true;
				}
			}
			return false;
		} 
		catch (ClassNotFoundException e) {
			throw new ProgramException("Error while inspecting ctClass to check for annotation '" + this.annotationType.getSimpleName() + "'", e);
		} 
	}
	
	protected boolean implementsAnnotationInterface(Class<?>[] interfaces) {
		for (Class<?> interfaze : interfaces) {
			if (this.annotationTypeName.equals(interfaze.getName())) {
				return true;
			}
		}
		return false;
	}

	public CtClass getAnnotationType() {
		try {
			if (this.annotationType == null) {
				this.annotationType = this.classPool.get(this.annotationTypeName);
			}
		}
		catch (NotFoundException exception) {
			throw new ProgramException("annotation type not found", exception);
		}
		return this.annotationType;
	}

	@Override
	public boolean evaluate(CtField ctField) {
		try {
			for (Object annotationProxy : ctField.getAnnotations()) {
				if (implementsAnnotationInterface(annotationProxy.getClass().getInterfaces())) {
					return true;
				}
			}
			return false;
		} 
		catch (ClassNotFoundException e) {
			throw new ProgramException("Error while inspecting ctClass to check for annotation '" + this.annotationType.getSimpleName() + "'", e);
		} 
	}

}
