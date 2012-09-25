package com.uqbar.poo.aop

import javassist.Modifier
import javassist.NotFoundException
import javassist.expr.FieldAccess
import com.uqbar.aop.FieldInterceptor
import com.uqbar.aop.Interceptor

/**
 * @author nny
 *
 */
class ObservableFieldInterceptor extends FieldInterceptor {

  write((statement: StringBuffer, fieldAccess: FieldAccess) => {

    if (!Modifier.isTransient(fieldAccess.getField().getModifiers())) {
      var newStatement =
    	"""
		  $fieldTypeName oldValue = $oldValue;
		  $originalAsigment;
		  $this.firePropertyChange('$fieldName', $coerceToObject(oldValue), $coerceToObject($newValue));
    	"""

      statement.replace(0, statement.length(), newStatement)
    }
  })

  override def getSpecificPropertyKey(): String = {
    return "ObservableFieldAccessInterceptor";
  }

}
