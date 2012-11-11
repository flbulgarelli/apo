package com.uqbar.apo

import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec
import javassist.ClassPool
import org.junit.runner.RunWith
import javassist.expr.FieldAccess
import com.uqbar.apo.pointcut.FieldPointCut
import com.uqbar.apo.pointcut.ClassPointCut
import com.uqbar.apo.pointcut.MatchPointCut
import com.uqbar.apo.pointcut.PointCut

class PrintFieldInterceptor extends FieldInterceptor {
  write((statement, fieldAccess) => statement.append("$this.dispatch($argument1);"))
}

class FieldTestConfiguration extends TestConfiguration {
  def createPointcut: ClassPointCut = new PointCut with MatchPointCut with ClassPointCut with FieldPointCut {
    matchFieldType[String]
  }

  def adInterceptor(advice: Advice) = advice.addInterceptor(new PrintFieldInterceptor())
}

@RunWith(classOf[JUnitRunner])
class FieldInterceptorTest extends InterceptorTest with App{
} 
