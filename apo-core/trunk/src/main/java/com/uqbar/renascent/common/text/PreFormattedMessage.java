package com.uqbar.renascent.common.text;

/**
 * @author npasserini
 */
class PreFormattedMessage {
	private MessageFormat format;
	private String[] arguments;

	PreFormattedMessage(MessageFormat format, String[] arguments) {
		this.format = format;
		this.arguments = arguments;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.format.format(arguments);
	}

}
