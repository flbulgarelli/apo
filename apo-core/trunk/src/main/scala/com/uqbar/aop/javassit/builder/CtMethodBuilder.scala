package com.uqbar.aop.javassit.builder
import javassist.CtClass

import scala.collection.JavaConversions._
import javassist.CtMethod
import javassist.CtNewMethod
import com.uqbar.aop.javassit.parser.JavassistParser

class CtMethodBuilder {

  var methodName: String = _
  var modifier: Int = _
  var returnType: CtClass = _
  var owner: CtClass = _
  var parametersType: Array[CtClass] = Array()
  var body: String = _

  def withName(name: String)  = {this.methodName = name;this }

  def withModifier(modifier: Int) { this.modifier = modifier; this}

  def withReturnType(returnType: CtClass) = {this.returnType = returnType; this}

  def withOwner(owner: CtClass)= {this.owner = owner; this}

  def witBody(body: String)= {this.body = body; this}

  def withparameterClass(parametersType: CtClass*) = {this.parametersType = parametersType.toArray; this }

  def build() = createMethod(methodName, modifier, returnType, owner, body, parametersType)

  protected def createMethod(methodName: String, modifier: Int, returnType: CtClass, owner: CtClass,
    body: String, parametersType: Array[CtClass]): CtMethod = {

    return CtNewMethod.make(
      modifier,
      returnType,
      methodName, //name
      parametersType.toArray, //parameters type
      Array[CtClass](), //exceptions
      JavassistParser.parser(body),
      owner);
  }

}