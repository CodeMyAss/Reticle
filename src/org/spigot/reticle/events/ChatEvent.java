package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.connector;

public class ChatEvent extends CancellableEvent {
	private String message;
	private int pos = 0;
	private boolean ignored=false;

	public ChatEvent(mcbot bot, String message) {
		super(bot);
		this.message = message;
		ignored=(bot.connector.isMessageIgnored(getFormatedMessage()));
	}

	public ChatEvent(mcbot bot, String message, int pos) {
		super(bot);
		this.message = message;
		this.pos = pos;
		ignored=(bot.connector.isMessageIgnored(getFormatedMessage()));
	}
	
	/**
	 * @return True if message should be ignored
	 */
	public boolean isIgnored() {
		return ignored;
	}
	
	/**
	 * Returns message as it is received from server
	 * @return Returns message as it is received from server
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns formated message
	 * @return Returns formated message
	 */
	public String getFormatedMessage() {
		return connector.parsechat(message);
	}

	/**
	 * Returns chat position
	 * @return Returns chat position
	 */
	public int getPosition() {
		return pos;
	}

}
