package com.uqbar.aop.javassit.parser;

import com.uqbar.commons.StringUtils;

import javassist.NotFoundException;
import javassist.expr.FieldAccess;

public enum Tokens {
	
	THIS("$this") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return "$0";
		}
	},
	FIELD_NAME("$fieldName") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return fieldAccess.getFieldName();
		}
	},
	FIELD_TYPE_NAME("$fieldTypeName") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			try {
				return fieldAccess.getField().getType().getName();
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	},
	ARGUMENT("$argument") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return "$";
		}
	},
	INTERCEPTOR("$interceptor") {
		/**
		 * @return un String con el codigo que retorna la instancia de interceptor.
		 */
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return "((" + "com.uqbar.aop.transaction.ObjectTransactionImpl" + ")"
			+ "com.uqbar.aop.transaction.ObjectTransactionManager.getTransaction()" + ")";
		}
	},
	STRINGS("'") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return "\"";
		}
	},
	RETURN("$rtn") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return "$_ =";
		}
		
	},
	
	
	//Shortcuts
	
	
	DEFAULT_VALUE_ASSIGMENT("$defaultValueAssignment") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return ARGUMENT.getJavassistExp(fieldAccess) + "1";
		}
		
	},
	ORIGINAL_STATEMENT_ASSIGMENT("$originalAsigment") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return DEFAULT_FIELD.getJavassistExp(fieldAccess) + " = " + Tokens.DEFAULT_VALUE_ASSIGMENT.getJavassistExp(fieldAccess)+ ";";
		}
		
	},
	ORIGINAL_STATEMENT_READER("$originalReader") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return Tokens.RETURN.getJavassistExp(fieldAccess) + " " + Tokens.DEFAULT_FIELD.getJavassistExp(fieldAccess)+ ";";
		}
		
	},
	DEFAULT_FIELD("$defaultField") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return THIS.getJavassistExp(fieldAccess) + "." + FIELD_NAME.getJavassistExp(fieldAccess);
		}
		
	},
	OLD_VALUE("$oldValue") {
		@Override
		public String getJavassistExp(FieldAccess fAcces) {
			return THIS.getJavassistExp(fAcces) + ".get" + StringUtils.capitalize(FIELD_NAME.getJavassistExp(fAcces)) +"();";
		}
	},
	OBJECT("$Object") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return Object.class.getName();
		}
	},
	
	NEW_VALUE("$newValue") {
		@Override
		public String getJavassistExp(FieldAccess fieldAccess) {
			return ARGUMENT.getJavassistExp(fieldAccess)+"1";
		}
	};

	private String regExp;
	
	private Tokens(String regExp) {
		this.setRegExp(regExp);
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getRegExp() {
		return regExp;
	}


	public abstract String getJavassistExp(FieldAccess fieldAccess);
	

}
