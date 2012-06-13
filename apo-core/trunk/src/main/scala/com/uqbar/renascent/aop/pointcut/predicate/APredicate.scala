package com.uqbar.renascent.aop.pointcut.predicate
import javassist.CtField
import javassist.CtClass

trait APredicate {
  	def evaluate(ctClass:CtClass ):Boolean
	def evaluate(ctField:CtField ):Boolean
}