package com.uqbar.aop
import org.scalatest.GivenWhenThen
import org.scalatest.FunSpec
import com.uqbar.aop.entities.TestObject
import com.uqbar.aop.pointcut.PointCut
import com.uqbar.aop.pointcut.MatchPointCut
import javassist.ClassPool
import com.uqbar.aop.pointcut.ClassPointCut
import com.uqbar.aop.pointcut.MethodPointCut

abstract class TestConfiguration extends Configuration {
  val methodInterceptor = new PrintMethodInterceptor()

  def createPointcut: ClassPointCut
  def adInterceptor(advice: Advice)

  override def createAdvices(): List[Advice] = {
    val testPoincut = createPointcut
    testPoincut.matchClassName(_.equals("TestObject"))
    testPoincut.matchPackage(_.contains("com.uqbar.aop.entities"))

    val advice = new Advice(testPoincut)
    this.adInterceptor(advice)

    List(advice)
  }
}

class InterceptorTest extends FunSpec with GivenWhenThen with Listener {

  var eventDispatch: String = _

  def listen(event: String) = eventDispatch = event

  describe("Probamos interceptar metodos") {
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