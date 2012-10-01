package com.uqbar.pot.aop

import org.uqbar.commons.utils.Transactional
import com.uqbar.aop.Advice
import com.uqbar.aop.AdviceWeaver
import javassist.ClassPool
import com.uqbar.aop.JoinPoint
import com.uqbar.aop.pointcut.predicate.FieldPointCut
import javassist.expr.FieldAccess
import com.uqbar.aop.pointcut.predicate.AnnotationPointCut
import com.uqbar.aop.pointcut.predicate.PointCut

/**
 * 
 * @author ?
 */
trait TTransactionalAdviceWeaver extends AdviceWeaver {

  val transactionInterceptor = new TransactionFieldInterceptor()

  override def configureAdvices() {
    this.advices.append(new Advice(
      new PointCut with AnnotationPointCut with FieldPointCut{hasAnnotation(classOf[Transactional].getName())},
      		new JoinPoint().addInterceptor(transactionInterceptor)))
  }

  override def configureJoinPoint(joinPoint: JoinPoint) {
    super.configureJoinPoint(joinPoint)
    joinPoint.addInterceptor(transactionInterceptor)
  }
}

class TransactionalAdviceWeaver(cp: ClassPool) extends AdviceWeaver(cp) with TTransactionalAdviceWeaver