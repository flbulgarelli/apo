package com.uqbar.renascent.framework.aop;

import javassist.NotFoundException;
import javassist.expr.FieldAccess;

/**
 * 
 */
public class GenericObjectReferenceFieldAccessInterceptor extends FieldAccessInterceptor {

    protected void modifyWriterFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) throws NotFoundException {
    	//nada
    	writerStatement.insert(0, this.mustWeave(fieldAccess) ? "$0.fieldAccessed();" : "");
    }

    protected void modifyReaderFieldAccess(StringBuffer writerStatement, FieldAccess fieldAccess) throws NotFoundException {
    	this.modifyWriterFieldAccess(writerStatement, fieldAccess);
    }

    private boolean mustWeave(FieldAccess fieldAccess) {
    	return fieldAccess.getFieldName().equals("partition");
    }

    /**
     * Se habilita si está habilitada la LazyRelationResolverFieldAccessInterceptor
     * de esta forma se maneja el mismo flag para toda la intercepcion de la lazyOneToOne
     */
    protected String getSpecificPropertyKey() {
    	return "LazyRelationResolverFieldAccessInterceptor";
    }
}
