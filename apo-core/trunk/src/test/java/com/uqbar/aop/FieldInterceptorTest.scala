package com.uqbar.aop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec
import com.uqbar.aop.pointcut.ClassPointCut
import com.uqbar.aop.pointcut.FieldPointCut
import com.uqbar.aop.pointcut.MatchPointCut
import com.uqbar.aop.pointcut.MethodPointCut
import com.uqbar.aop.pointcut.PointCut
import javassist.ClassPool
import org.junit.runner.RunWith
import javassist.expr.FieldAccess

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 *
 *
 * -Djava.system.class.loader=com.uqbar.aop.TestFieldInterceptorClassLoader
 *
 */
class TestFieldInterceptorClassLoader(parent: ClassLoader) extends APOClassLoader(parent) {
  def createAdviceWeaver(cp: ClassPool): AdviceWeaver = {
    return new FieldTestAdviceWeaver(cp);
  }
}

class PrintFieldInterceptor extends FieldInterceptor {
  write((statement, fieldAccess) => statement.append("$this.dispatch($argument1);"))
}

class FieldTestAdviceWeaver(pool: ClassPool) extends TestAdviceWeaver(pool) {
  def createPointcut: ClassPointCut = new PointCut with MatchPointCut with ClassPointCut with FieldPointCut {
    matchFieldType[String]
  }

  def adInterceptor(advice: Advice) = advice.addInterceptor(new PrintFieldInterceptor())
}

@RunWith(classOf[JUnitRunner])
class FieldInterceptorTest extends InterceptorTest {
} 
