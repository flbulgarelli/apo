package com.uqbar.poo.aop

import org.uqbar.commons.utils.Observable
import com.uqbar.aop.Advice
import javassist.ClassPool
import javassist.CtClass
import com.uqbar.aop.pointcut.PointCut
import com.uqbar.aop.pointcut.AnnotationPointCut
import com.uqbar.aop.pointcut.FieldPointCut
import com.uqbar.aop.Configuration

/**
 *
 * @author nny
 */
trait ObservableConfiguration extends Configuration {
  val observableFieldInterceptor = new ObservableFieldInterceptor()
  val observableBehavior = new ObservableBehavior()

  override def createAdvices(): List[Advice] = {
    val fieldPoint = new PointCut with AnnotationPointCut with FieldPointCut {
      hasAnnotation(classOf[Observable].getName())
    }

    val observableAdvice = new Advice(fieldPoint) {
      override def apply(ctClass: CtClass) {
        observableBehavior.addBehavior(ctClass)
        super.apply(ctClass)
      }
    }

    super.createAdvices().::(observableAdvice.addInterceptor(observableFieldInterceptor))
  }
}
