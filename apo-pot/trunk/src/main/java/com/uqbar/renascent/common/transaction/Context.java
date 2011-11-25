package com.uqbar.renascent.common.transaction;

import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uqbar.commons.collections.CollectionFactory;
import com.uqbar.commons.exceptions.ProgramException;
import com.uqbar.commons.loggeable.LoggeableImpl;
import com.uqbar.renascent.common.text.MessageFormatter;

/**
 * Encapsula el contexto correspondiente a un proceso que debe ejecutarse. Este contexto no se pasa por
 * parámetro, en cambio, se obtiene a partir de getCurrentContext de esta clase.
 * 
 * Para la mayoría de los usos, el contexto se considera válido una vez que se le ha asignado un owner. Un
 * contexto sin owner podría considerarse un NullObject, que no debe ser utilizado.
 * 
 * @author npasserini
 */
public class Context implements ContextConstants {
	private static final Log LOG = LogFactory.getLog(Context.class);

	public static final MessageFormatter PARAMETER_ALREADY_SET = new MessageFormatter(
		"Attempted to overwrite parameter {0}");
	public static final MessageFormatter PARAMETER_NOT_SET = new MessageFormatter(
		"Attempted to remove unexistent parameter {0}");

	private static ThreadLocal<Context> threadLocalContext = new ThreadLocal<Context>();

	private Map<String, Object> parameters;
	private Map<String, TransactionalResource> resourceMap = CollectionFactory.createMap();

	private static final Object DUMMY_OWNER = new Object() {
		public String toString() {
			return "DummyContextOwner";
		}
	};

	/*
	 * Static methods. Context creation, invalidation & retrieval.
	 */

	/**
	 * Inicializa en Contexto y le asocia un dueño, el cual podra terminar el Contexto; Si el Contexto ya fue
	 * inicializado solo lo usa; Si el Contexto fue creado con dueño Dummy se le asocia al primero que lo
	 * tome.
	 */
	public static Context beginContext(Object owner) {
		AssertUtils.assertArgumentNotNull("Context OWNER cannot be NULL!", owner);

		// TODO: synchronize
		Context current = threadLocalContext.get();
		if (current == null) {
			LOG.debug("Begining Context with owner: " + owner);
			current = new Context(owner);
			threadLocalContext.set(current);
		}
		else {
			if (current.isContextOwner(DUMMY_OWNER) && DUMMY_OWNER != owner) {
				current.setContextOwner(owner);
				LOG.debug("Asigning context to owner: " + owner);
			}
		}
		return current;
	}

	/**
	 * Termina el Contexto y libera todos los recurso asociados.
	 */
	public static void endContext(Object owner) {
		Context current = threadLocalContext.get();
		AssertUtils.assertNotNull("No initialized Context found to end!", current);

		if (!current.isContextOwner(DUMMY_OWNER)) {
			AssertUtils.assertTrue("Cannot end Context, the given object '" + owner + "' isn't the expected owner: "
				+ current.getContextOwner(), current.isContextOwner(owner));
		}
		LOG.debug("Ending Context with owner: " + owner);
		current.parameters.clear();
		threadLocalContext.set(null);

		for (TransactionalResource resource : current.resourceMap.values()) {
			try {
				resource.end();
			}
			catch (Throwable throwable) {
				LOG.error("Error while freeing resource: " + resource, throwable);
			}
		}
		current.resourceMap.clear();
	}

	/**
	 * Asocia un Recurso al Contexto.
	 */
	public void addResource(String resourceName, TransactionalResource resource) {
		AssertUtils.assertArgumentNotNull("El nombre del recurso no puede ser null.", resourceName);
		AssertUtils.assertArgumentNotNull("No se puede asociar un Recurso en null.", resource);
		resource.start();
		this.resourceMap.put(resourceName, resource);
	}

	/**
	 * Obtiene el Resurso registrado con el nombre dado.
	 */
	public TransactionalResource getResource(String resourceName) {
		return this.resourceMap.get(resourceName);
	}

	public boolean containResource(String resourceName) {
		return this.resourceMap.containsKey(resourceName);
	}

	/**
	 * Obtiene el Contexto actual y si no existe uno crea uno.
	 */
	public static Context getCurrentContext() {
		Context current = threadLocalContext.get();
		if (current == null) {
			current = beginContext(DUMMY_OWNER);
		}
		return current;
	}

	/**
	 * Crea un nuevo contexto, si no hubiera ya uno presente.
	 * 
	 * @deprecated Usar <code>beginContext()</code>.
	 */
	public static Context createIfNotPresent(Object contextOwner) {
		Context current = threadLocalContext.get();
		if (current == null) {
			if (contextOwner != null) {
				LOG.debug("Creating context for " + contextOwner);
			}
			else {
				LOG.debug("Creating no-owner context");
			}
			current = new Context(contextOwner);
			threadLocalContext.set(current);
		}
		else if (current.getContextOwner() == null) {
			if (contextOwner != null) {
				LOG.debug("Asigning context to " + contextOwner);
				current.setContextOwner(contextOwner);
			}
		}
		else {
			if (contextOwner != null) {
				LoggeableImpl loggeable = new LoggeableImpl("Reusing context");
				loggeable.addInfo("owner", current.getContextOwner());
				loggeable.addInfo("client", contextOwner);

				LOG.debug(loggeable);
			}
		}
		return current;
	}

	/**
	 * Crea un contexo hijo al existente si lo hubiera o uno independiente si no lo hay. Si hay un contexto
	 * sin owner se reutiliza, sin crear uno nuevo.
	 * 
	 * @deprecated Usar <code>beginContext()</code>.
	 */
	public static Context createChildIfPresent(Object contextOwner) {
		Context current = threadLocalContext.get();
		if (current == null) {
			LOG.debug("Creating context for " + contextOwner);
			current = new Context(contextOwner);
			threadLocalContext.set(current);
		}
		else if (current.getContextOwner() == null) {
			if (contextOwner != null) {
				LOG.debug("Asigning context to " + contextOwner);
				current.setContextOwner(contextOwner);
			}
		}
		else {
			LOG.debug("Creating child context for " + contextOwner);
			current = new Context(contextOwner, getCurrentContext());
			threadLocalContext.set(current);
		}

		return current;
	}

	/**
	 * Elimina el contexto actual, siempre y cuando el objeto 'owner' sea el due�o del contexto. Si el
	 * contexto actual tiene un contexto padre, ese contexto pasa a ser el actual.
	 * 
	 * @deprecated Usar <code>endContext()</code>.
	 */
	public static void invalidateCurrentContext(Object owner) {
		Context context = getCurrentContext();
		if (context.isContextOwner(owner)) {
			LOG.debug(new LoggeableImpl("Invalidating context").addInfo("owner", owner));
			threadLocalContext.set(context.getParentContext());
		}
		else {
			LoggeableImpl loggeable = new LoggeableImpl("Context not invalidated");
			loggeable.addInfo("owner", owner);
			loggeable.addInfo("fakeInvalidator", context.getContextOwner());

			LOG.debug(loggeable);
		}
	}

	/**
	 * Invalida el contexto actual, si existe un contexto padre se asigna ese.
	 */
	public static void unsecureInvalidateCurrentContext() {
		Context context = getCurrentContext();
		threadLocalContext.set(context.getParentContext());
	}

	/*
	 * Parameter management
	 */

	/**
	 * Devuelve el parámetro 'parameterName'
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParameter(String parameterName) {
		return (T) this.parameters.get(parameterName);
	}

	/**
	 * Agrega un nuevo parámetro a este contexto
	 */
	public void setParameter(String parameterName, Object parameterValue) {
		this.parameters.put(parameterName, parameterValue);
	}

	/**
	 * Elimina un parámetro de este contexto
	 */
	public void removeParameter(String parameterName) {
		this.parameters.remove(parameterName);
	}

	/**
	 * Determina si existe el parámetro 'parameterName' en este contexto.
	 */
	public boolean containsParameter(String parameterName) {
		return this.parameters.containsKey(parameterName);
	}

	/*
	 * Secure methods
	 */

	/**
	 * Agrega un nuevo parámetro a este contexto
	 */
	public void secureSetParameter(String parameterName, Object parameterValue) {
		// TODO secureSetParameter
		// ProgramException.assertFalse(
		// this.containsParameter(parameterName),
		// PARAMETER_ALREADY_SET.preFormat(parameterName));
		this.setParameter(parameterName, parameterValue);
	}

	/**
	 * Agrega un nuevo parámetro a este contexto
	 */
	public void secureRemoveParameter(String parameterName) {
		ProgramException.assertTrue(this.containsParameter(parameterName), PARAMETER_NOT_SET.preFormat(parameterName));
		this.removeParameter(parameterName);
	}

	/*
	 * Parent management
	 */

	/**
	 * Devuelve el owner del contexto.
	 */
	public Object getContextOwner() {
		return this.getParameter(CONTEXT_OWNER);
	}

	/**
	 * Permite al contexto modificar su owner. S�lo deber�a ser utilizado dentro del framework y para tareas
	 * espec�ficas de manejo de contextos.
	 */
	protected void setContextOwner(Object owner) {
		this.setParameter(CONTEXT_OWNER, owner);
	}

	/**
	 * Determina si el objeto es el dueño del contexto y por lo tanto es responsable de las tareas de
	 * administración.
	 */
	public boolean isContextOwner(Object obj) {
		return this.getParameter(CONTEXT_OWNER) == obj;
	}

	/**
	 * Determina si este contexto tiene un contexto padre.
	 */
	public boolean isChildContext() {
		return this.containsParameter(CONTEXT_PARENT);
	}

	/**
	 * Devuelve el contexto padre de este contexto.
	 */
	public Context getParentContext() {
		return (Context) this.getParameter(CONTEXT_PARENT);
	}

	/*
	 * Constructors (always protected)
	 */

	/**
	 * Constructor protegido.
	 */
	protected Context(Object owner) {
		this.parameters = CollectionFactory.createMap();
		this.setParameter(CONTEXT_OWNER, owner);
	}

	/**
	 * Constructor protegido.
	 */
	protected Context(Object owner, Context parentContext) {
		this(owner);
		this.setParameter(CONTEXT_PARENT, parentContext);
	}

	public Log getLog() {
		// LOG Manejar por contexto.
		return Context.LOG;
	}

	/*
	 * Stack available parameters
	 */

	public void pushParameter(String parameterName, Object parameterValue, Object owner) {
		if (this.containsParameter(parameterName)) {
			Stack<ParameterStackElement> stack = this.getParameterStack(parameterName);
			Object oldValue = this.getParameter(parameterName);
			Object oldOwner = this.getParameter(parameterName + ".owner");
			stack.push(new ParameterStackElement(oldValue, oldOwner));
		}
		this.setParameter(parameterName, parameterValue);
		this.setParameter(parameterName + ".owner", owner);
	}

	public void popParameter(String parameterName, Object owner) {
		if (getParameter(parameterName + ".owner") == owner) {
			Stack<ParameterStackElement> stack = this.getParameterStack(parameterName);
			if (stack.isEmpty()) {
				this.removeParameter(parameterName);
				this.removeParameter(parameterName + ".owner");
			}
			else {
				ParameterStackElement element = (ParameterStackElement) stack.pop();
				this.setParameter(parameterName, element.getValue());
				this.setParameter(parameterName + ".owner", element.getOwner());
			}

		}
	}

	private Stack<ParameterStackElement> getParameterStack(String parameterName) {
		if (this.containsParameter(parameterName + ".stack")) {
			return this.getParameter(parameterName + ".stack");
		}
		else {
			Stack<ParameterStackElement> stack = new Stack<ParameterStackElement>();
			this.setParameter(parameterName + ".stack", stack);
			return stack;
		}
	}

	/**
	 * 
	 * @author npasserini
	 */
	private class ParameterStackElement {
		private Object value, owner;

		private ParameterStackElement(Object value, Object owner) {
			this.value = value;
			this.owner = owner;
		}

		private Object getValue() {
			return this.value;
		}

		private Object getOwner() {
			return this.owner;
		}
	}
}