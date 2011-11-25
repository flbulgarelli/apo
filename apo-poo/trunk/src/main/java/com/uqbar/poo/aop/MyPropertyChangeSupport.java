package com.uqbar.poo.aop;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author nnydjesus
 *
 */
public class MyPropertyChangeSupport extends PropertyChangeSupport {
	private static final long serialVersionUID = 1L;

	public MyPropertyChangeSupport(Object sourceBean) {
		super(sourceBean);
	}
	
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	super.firePropertyChange(propertyName,  oldValue, newValue);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    	super.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    	super.removePropertyChangeListener(propertyName, listener);
    }

}
