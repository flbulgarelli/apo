package com.uqbar.poo.aop
import org.uqbar.commons.utils.Observable
import org.scalatest.BeforeAndAfter
import org.scalatest.GivenWhenThen
import org.scalatest.FeatureSpec
import org.uqbar.commons.utils.ReflectionUtils
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@Observable
case class Bean(var name:String){
  def getName()= name
  def setName(aName:String) = name = aName
}


@RunWith(classOf[JUnitRunner])
class ObservableTest extends FeatureSpec with GivenWhenThen with BeforeAndAfter {
  
    scenario("se invoca el metodo pop en un stack no vacio") {
 
      given("un stack no vacio")
      val bean = Bean("Pepe") 
 
      when("cuando se invoca pop en el stack")
  		val support = ReflectionUtils.invokeMethod(bean, "getChangeSupport");
 
      then("Deberia retornar el ultimo elemento pusheado")
      assert(support != null)
    }
 

}