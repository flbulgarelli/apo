package com.uqbar.poo.aop

import com.uqbar.apo.pointcut.AnnotationPointCut
import com.uqbar.apo.pointcut.FieldPointCut
import javassist.CtClass
import com.uqbar.apo.pointcut.PointCut
import com.uqbar.apo.Advice
import com.uqbar.apo.Configuration
import org.uqbar.commons.utils.Observable


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

class ObservableConfigurationImpl extends ObservableConfiguration
