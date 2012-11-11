package com.uqbar.apo
import org.junit.runner.RunWith
import com.uqbar.apo.pointcut.ClassPointCut
import com.uqbar.apo.pointcut.MethodPointCut
import com.uqbar.apo.pointcut.PointCut
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