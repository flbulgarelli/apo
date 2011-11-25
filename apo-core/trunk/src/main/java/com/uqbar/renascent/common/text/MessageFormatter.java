package com.uqbar.renascent.common.text;

/**
 * Los m�todos <code>format()</code> deben recibir obligatoriamente strings, de forma de asegurar el uso
 * correcto en tiempo de compilaci�n. 
 * En el caso de los m�todos <code>preFormat()</code>, se permite recibir un objeto, posibilitando 
 * la postergaci�n de la generaci�n del texto espec�fico hasta el momento del <code>toString()</code> 
 * @author npasserini
 */
public class MessageFormatter {
	private MessageFormat messageFormat;
	private String pattern;

	public MessageFormatter(String pattern) {
		this.pattern = pattern;
	}

	public String format(String value) {
		return this.getMessageFormat().format(new String[]{value});
	}

	public String format(String value1, String value2) {
		return this.getMessageFormat().format(new String[]{value1, value2});
	}

	public String format(String value1, String value2, String value3) {
		return this.getMessageFormat().format(new String[]{value1, value2, value3});
	}
	
	/**
	 * Devuelve un objeto cuyo m�todo {@link Object#toString()} es capaz de devolver el mensaje formateado.
	 * Se utiliza en los casos en que se desea pasar un mensaje por par�metro, que todav�a no se sabe si se
	 * va a utilizar y se desea evitar la construcci�n innecesaria del mensaje definitivo. 
	 */
	public Object preFormat(Object value) {
		return new PreFormattedMessage(this.getMessageFormat(), new String[]{value.toString()});
	}

	/**
	 * TODO No hacer toString de los par�metros hasta el momento de rendering
	 */
	public Object preFormat(Object value1, Object value2) {
		return new PreFormattedMessage(this.getMessageFormat(), new String[]{value1.toString(), value2.toString()});
	}
	
	//***************************
	//** private Helpers
	//***************************
	private MessageFormat getMessageFormat() {
		if (this.messageFormat == null) {
			this.messageFormat = new MessageFormat(this.pattern);
		}
		return this.messageFormat;
	}

}
