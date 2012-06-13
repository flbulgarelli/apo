package com.uqbar.aop.javassit.parser
import javassist.expr.FieldAccess

object JavassistParser {

  def parser(string: String): String = parser(null, string)

  def parser(fieldAccess: FieldAccess, string: String): String = {
    var result = string;
    Tokens.values().foreach(token => {
      if (result.contains(token.getRegExp())) {
        result = result.replace(token.getRegExp(), token.getJavassistExp(fieldAccess));
      }
    })

    return result;
  }

}