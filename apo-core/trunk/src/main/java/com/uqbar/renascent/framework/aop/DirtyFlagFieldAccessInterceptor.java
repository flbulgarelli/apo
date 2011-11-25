package com.uqbar.renascent.framework.aop;

import javassist.expr.FieldAccess;

/**
 * DOCME
 */
public class DirtyFlagFieldAccessInterceptor extends FieldAccessInterceptor {

    protected void modifyWriterFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) {
	// no me interesa detectar cambios en los objetos Partition. En realidad, mas que no interesarme me
	// complica la vida porque tengo que repetir codigo, dado que implementan Persistible pero no
	// extienden de
	// alguna clase comun.
	if (this.mustWeave(fieldAccess)) {
	    writerStatement.append("$0.setDirty();");
	}
    }

    private boolean mustWeave(FieldAccess fieldAccess) {
	return !fieldAccess.getClassName().endsWith("Partition") && !fieldAccess.where().getName().equals("setDirty")
		&& !fieldAccess.where().getName().equals("clean");
    }

    protected void modifyReaderFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) {
	// nada
    }

    protected String getSpecificPropertyKey() {
	return "DirtyFlagFieldAccessInterceptor";
    }
}