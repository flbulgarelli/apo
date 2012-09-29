package com.uqbar.aop
import com.uqbar.aop.pointcut.predicate.PointCut
import javassist.expr.Expr
import javassist.CtClass

class Advice(var pointCut:PointCut, var joinPoint:JoinPoint) {
  
  def apply(ctClass:CtClass){
    joinPoint.instrument(ctClass)
  }
}