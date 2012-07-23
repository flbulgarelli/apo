package com.uqbar.aop
import javassist.expr.ExprEditor
import com.uqbar.aop.pointcut.predicate.APredicate

class Advice(var predicate:APredicate, var instrument:ExprEditor) {

}