package com.uqbar.poo.aop

import java.util.List

import javassist.ClassPool
import javassist.CtClass

import org.uqbar.commons.utils.Observable

import com.uqbar.aop.Advice
import com.uqbar.aop.BehaviorAdviceWeaverStrategy
import com.uqbar.aop.WeavingInstrumentor
import com.uqbar.aop.pointcut.predicate.HasAnnotationPredicate

class ObservableBehaviorAdviceWeaverStrategy extends BehaviorAdviceWeaverStrategy {
	
	private var observableFieldAccessInterceptor = new ObservableFieldAccessInterceptor()
	private var observableBehavior = new ObservableBehavior()

	override def addAdvices(classPool:ClassPool, advices:List[Advice]) {
		advices.add( new Advice(
			new HasAnnotationPredicate(classPool, classOf[Observable].getName()),
			new WeavingInstrumentor().addFieldAccessInterceptor(observableFieldAccessInterceptor)))
	}
	
	override def applyAdviceToCtClass(ctClass:CtClass, advice:Advice )	{
		observableBehavior.addBehavior(ctClass)
	}

	override def configureInstrumentor(instrumentor:WeavingInstrumentor ) {
		instrumentor.addFieldAccessInterceptor(observableFieldAccessInterceptor)
	}	

}
