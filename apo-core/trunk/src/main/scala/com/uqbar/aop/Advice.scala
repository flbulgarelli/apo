package com.uqbar.aop
import com.uqbar.aop.pointcut.predicate.PointCut
import javassist.expr.Expr

class Advice(var pointCut:PointCut, var joinPoint:JoinPoint) {

}