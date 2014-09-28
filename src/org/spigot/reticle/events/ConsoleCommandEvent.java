package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

/**
 * Fired whenever user writes command into main tab (Main@Reticle) and sends it
 * 
 * @author Encorn
 * 
 */
public class ConsoleCommandEvent extends CancellableEvent {
	private final boolean hasparams;
	private final String[] params;
	private final String name;
	private final String lowname;

	/**
	 * Parses messages and retrieves first token
	 * 
	 * @return Command name
	 */
	public String getCommandName() {
		return name;
	}

	/**
	 * Parses messages and retrieves first token in lowercase
	 * @return Command name lowercase
	 */
	public String getCommandNameLowerCase() {
		return lowname;
	}
	
	/**
	 * @return Returns True is command comes with parameters, False if otherwise
	 */
	public boolean hasParameters() {
		return hasparams;
	}

	/**
	 * @return Returns Array of parameters or empty array if parameters do not
	 *         exist
	 */
	public String[] getParams() {
		return params;
	}

	public ConsoleCommandEvent(mcbot bot, String message) {
		super(bot);
		this.name = message.split(" ")[0];
		this.lowname=this.name.toLowerCase();
		this.hasparams = (name.length() < message.length());
		if (hasparams) {
			this.params = message.substring(name.length() + 1).split(" ");
		} else {
			this.params = new String[0];
		}
	}
}
