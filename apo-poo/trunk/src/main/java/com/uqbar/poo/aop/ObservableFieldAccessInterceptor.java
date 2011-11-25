package com.uqbar.poo.aop;

import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.FieldAccess;

import com.uqbar.renascent.framework.aop.FieldAccessInterceptor;

/**
 * @author nny
 *
 */
public class ObservableFieldAccessInterceptor extends FieldAccessInterceptor {

	@Override
	protected void modifyWriterFieldAccess(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
		if(!Modifier.isTransient(fieldAccess.getField().getModifiers())){
			String newExpresion = 
				"$this.firePropertyChange('$fieldName', $oldValue, $newValue);";
	
			statement.replace(0, statement.length(), newExpresion + statement.toString());

		}
	}

	@Override
	protected void modifyReaderFieldAccess(StringBuffer statement, FieldAccess fieldAccess) throws NotFoundException {
	}

	@Override
	protected String getSpecificPropertyKey() {
		return "ObservableFieldAccessInterceptor";
	}

}
