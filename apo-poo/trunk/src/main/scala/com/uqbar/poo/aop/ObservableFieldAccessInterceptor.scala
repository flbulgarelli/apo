package com.uqbar.poo.aop

import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.FieldAccess;

import com.uqbar.aop.FieldAccessInterceptor;

/**
 * @author nny
 *
 */
class ObservableFieldAccessInterceptor extends FieldAccessInterceptor {

	override def modifyWriterFieldAccess(statement:StringBuffer , fieldAccess:FieldAccess ){
		if(!Modifier.isTransient(fieldAccess.getField().getModifiers())){
		  var newStatement = """
			  $Object oldValue = $oldValue;
			  $originalAsigment;
			  $this.firePropertyChange('$fieldName', oldValue, $newValue);
		  """
	
		statement.replace(0, statement.length(), newStatement)
		}
	}

	override def modifyReaderFieldAccess(statement:StringBuffer, fieldAccess:FieldAccess){
//		final boolean subclassOf = fieldAccess.getField().getType().subclassOf(fieldAccess.getField().getType().getClassPool().get(List.class.getName()));
//		if(!Modifier.isTransient(fieldAccess.getField().getModifiers() ) && subclassOf){
//			String newExpresion = 
//				"$this.firePropertyChange('$fieldName', $oldValue, $oldValue);";
//	
//			statement.append(newExpresion);
//		}
	}

	override def getSpecificPropertyKey():String ={
		return "ObservableFieldAccessInterceptor";
	}

}
