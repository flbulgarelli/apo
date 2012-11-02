package com.uqbar.aop
import org.junit.runner.RunWith
import com.uqbar.aop.pointcut.ClassPointCut
import com.uqbar.aop.pointcut.MatchPointCut
import com.uqbar.aop.pointcut.MethodPointCut
import com.uqbar.aop.pointcut.PointCut
import javassist.ClassPool
import org.scalatest.junit.JUnitRunner

class PrintMethodInterceptor extends MethodInterceptor {
  after((method) => "$this.dispatch($argument1);")
}

class MethodTestConfiguration extends TestConfiguration{

  def createPointcut = new PointCut with ClassPointCut with MethodPointCut {
    matchMethod(_.getName().contains("update"))
  }
  
  def adInterceptor(advice:Advice) = advice.addInterceptor(new PrintMethodInterceptor())

}

@RunWith(classOf[JUnitRunner])
class MethodInterceptorTest extends InterceptorTest {
}