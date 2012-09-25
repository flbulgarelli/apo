package com.uqbar.poo.aop

import org.uqbar.commons.utils.Observable

import com.uqbar.aop.pointcut.predicate.AnnotationPointCut
import com.uqbar.aop.pointcut.predicate.FieldPointCut
import com.uqbar.aop.pointcut.predicate.PointCut
import com.uqbar.aop.Advice
import com.uqbar.aop.AdviceWeaver
import com.uqbar.aop.JoinPoint

import javassist.ClassPool
import javassist.CtClass

trait TObservableAdviceWeaver extends AdviceWeaver {

  val observableFieldInterceptor = new ObservableFieldInterceptor()

  val observableBehavior = new ObservableBehavior()

  override def configureAdvices() {

    this.advices.append(new Advice(
      new PointCut with AnnotationPointCut with FieldPointCut {
        hasAnnotation(classOf[Observable].getName())
      },
      new JoinPoint().addInterceptor(observableFieldInterceptor)))
  }

  override def doApplyAdviceToCtClass(ctClass: CtClass, advice: Advice) {
    super.doApplyAdviceToCtClass(ctClass, advice)
    observableBehavior.addBehavior(ctClass)
  }

  override def configureJoinPoint(joinPoint: JoinPoint) {
    super.configureJoinPoint(joinPoint)
    joinPoint.addInterceptor(observableFieldInterceptor)
  }

}

class ObservableAdviceWeaver(cp: ClassPool) extends AdviceWeaver(cp) with TObservableAdviceWeaver {
  this.configureAdvices()

}