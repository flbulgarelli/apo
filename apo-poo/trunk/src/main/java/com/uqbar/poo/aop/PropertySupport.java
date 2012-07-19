package com.uqbar.poo.aop;

import java.util.EventListener;

public interface PropertySupport {

	void firePropertyChange(String propertyName, Object oldValue, Object newValue);

	void addPropertyChangeListener(String propertyName, EventListener listener);

	void removePropertyChangeListener(String propertyName, EventListener listener);

}