package com.uqbar.poo.aop;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventListener;

/**
 * 
 * @author nnydjesus
 */
public class POOPropertyChangeSupport extends PropertyChangeSupport implements PropertySupport {
	private static final long serialVersionUID = 1L;

	public POOPropertyChangeSupport(Object sourceBean) {
		super(sourceBean);
	}
	
    @Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	super.firePropertyChange(propertyName, oldValue, newValue);
    }
    
	public void addPropertyChangeListener(String propertyName, EventListener listener) {
    	super.addPropertyChangeListener(propertyName, (PropertyChangeListener) listener);
    }
    
	public void removePropertyChangeListener(String propertyName, EventListener listener) {
    	super.removePropertyChangeListener(propertyName, (PropertyChangeListener) listener);
    }
}