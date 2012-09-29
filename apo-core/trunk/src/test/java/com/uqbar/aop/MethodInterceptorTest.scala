package com.uqbar.aop
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import com.uqbar.aop.pointcut.predicate.ClassPointCut
import com.uqbar.aop.pointcut.predicate.MatchPointCut
import com.uqbar.aop.pointcut.predicate.MethodPointCut
import com.uqbar.aop.pointcut.predicate.PointCut
import com.uqbar.aop.entities.TestObject
import javassist.ClassPool
import org.scalatest.junit.JUnitRunner

/**
 * Nuestro classloader, que al cargar una clase, le hace weaving para meterle la magia de aspectos.
 *
 *
 * -Djava.system.class.loader=com.uqbar.aop.TestClassLoader
 */
class TestClassLoader(parent: ClassLoader) extends APOClassLoader(parent) {

  def createAdviceWeaver(cp: ClassPool): AdviceWeaver = {
    return new TestAdviceWeaver(cp);
  }

}

class PrintMethodInterceptor extends MethodInterceptor {
  after((method) => "$this.dispatch($argument1);")
}

class TestAdviceWeaver(pool: ClassPool) extends AdviceWeaver(pool) {
  val methodInterceptor = new PrintMethodInterceptor()

  override def configureAdvices() {
    advices.append(new Advice(
      new PointCut with MatchPointCut with ClassPointCut with MethodPointCut{
        matchClassName(_.equals("TestObject"))
        matchPackage(_.contains("com.uqbar.aop.entities"))
        matchMethod(_.getName().contains("update"))
      },
      new JoinPoint().addInterceptor(methodInterceptor)))
  }

}


@RunWith(classOf[JUnitRunner])
class MethodInterceptorTest extends FunSpec with GivenWhenThen with Listener{

  var eventDispatch: String = _

  def listen(event: String) = eventDispatch = event

  describe("vamos a probar que el Aspecto de metodos anda!") {
    it("") {

      given("un objeto de prueba")
      val testObject = new TestObject("Pepe", "pp@p.p")
      testObject.addListener(this)

      when("Cuando se invoca un el setter del nombre")
      testObject.updateName("probando")

      then("Deberia haber tirado el evento 'probando'")
      assert(eventDispatch === "probando")

      when("Cuando se invoca un el setter del email")
      testObject.updateFatherName("email.com")

      then("Deberia haber tirado el evento 'email.com'")
      assert(eventDispatch === "email.com")
    }
  }

}