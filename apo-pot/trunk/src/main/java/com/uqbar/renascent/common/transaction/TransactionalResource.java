package com.uqbar.renascent.common.transaction;

/**
 * Representa un recurso, como por ejemplo: una conexión a una Base de Datos o a un servidor de Mensajeria.
 * Este recurso puede estar asociado al Contexto o a una Trasacción, los cuales deberan manejar estos recursos.  
 * @author rgomez
 */
public interface TransactionalResource {

    /**
     * Prepara el recurso para ser usado.
     */
    public void start();

    /**
     * Libera el recurso.
     */
    public void end();

    /**
     * Commitea el recurso;
     */
    public void commit();

    /**
     * Rollbackea el Recurso.
     */
    public void rollback();
    
    /**
     * Verifica si el Recurso fue inicializado.
     */
    public boolean isStarted();
    
    /**
     * Verifica si el recurso fue liberado.
     */
    public boolean isEnded();
    
}
