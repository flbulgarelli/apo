package com.uqbar.renascent.framework.aop;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.MethodUtils;

import com.uqbar.commons.exceptions.ProgramException;

/**
 * Objeto que contiene a otro Objeto que está instanciado en otro Classloader.
 * 
 * @author rgomez
 */
public class ReflectiveObject {
    private Object instance;
    private Class<?> type;

    public ReflectiveObject(Object instance, Class<?> type) {
        this.instance = instance;
        this.type = type;
    }

    /**
     * Se invoca el método del Objeto contenido, dentro del contexto del ClassLader del tipo del Objeto.
     */
    public Object invoke(String methodName, Class<?>[] paramsTypes, Object[] values) {
        Object result;
        ClassLoader lastClassLoader = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(this.type.getClassLoader());
        Method method = MethodUtils.getAccessibleMethod(this.type, methodName, paramsTypes);
        try {
            result = method.invoke(this.instance, values);
        }
        catch (Exception exception) {
            System.err.println("FATAL: Error while executing method " + methodName + ", cause:");
            exception.printStackTrace(System.err);
            throw new ProgramException(exception);
        }
        finally {
            Thread.currentThread().setContextClassLoader(lastClassLoader);
        }

        return result;
    }

    /**
     * Se invoca el m�todo del Obejeto contenido, dentro del contexto del ClassLader del tipo del Objeto.
     */
    public Object invoke(String methodName, Class<?> paramType, Object value) {
        return this.invoke(methodName, new Class[] { paramType }, new Object[] { value });
    }

    /**
     * Se invoca el m�todo del Obejeto contenido, dentro del contexto del ClassLader del tipo del Objeto.
     */
    public Object invoke(String methodName) {
        return this.invoke(methodName, (Class[]) null, (Object[]) null);
    }

}