package com.uqbar.pot.aop

import org.uqbar.commons.utils.Transactional
import com.uqbar.aop.Advice
import com.uqbar.aop.AdviceWeaver
import javassist.ClassPool
import com.uqbar.aop.pointcut.PointCut
import com.uqbar.aop.pointcut.AnnotationPointCut
import com.uqbar.aop.pointcut.FieldPointCut
import com.uqbar.aop.Configuration

/**
 *
 * @author nny
 */
trait TransactionalConfiguration extends Configuration {

  val transactionInterceptor = new TransactionFieldInterceptor()

  override def createAdvices(): List[Advice] = {
    super.createAdvices().::(new Advice(new PointCut with AnnotationPointCut with FieldPointCut { hasAnnotation(classOf[Transactional].getName()) })
      .addInterceptor(transactionInterceptor))
  }

}
