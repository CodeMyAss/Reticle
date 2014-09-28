package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.connector;

public class ChatEvent extends CancellableEvent {
	private final String message;
	private final int pos;
	private final boolean ignored;
	private final String formatedmessage;

	public ChatEvent(mcbot bot, String message) {
		super(bot);
		this.pos = 0;
		this.message = message;
		formatedmessage=connector.parsechat(message);
		this.ignored = (bot.connector.isMessageIgnored(getFormatedMessage()));
	}

	public ChatEvent(mcbot bot, String message, int pos) {
		super(bot);
		this.message = message;
		this.pos = pos;
		formatedmessage=connector.parsechat(message);
		this.ignored = (bot.connector.isMessageIgnored(getFormatedMessage()));
	}

	/**
	 * @return True if message should be ignored
	 */
	public boolean isIgnored() {
		return ignored;
	}

	/**
	 * Returns message as it is received from server
	 * 
	 * @return Returns message as it is received from server
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns formated message
	 * 
	 * @return Returns formated message
	 */
	public String getFormatedMessage() {
		return formatedmessage;
	}

	/**
	 * Returns chat position
	 * 
	 * @return Returns chat position
	 */
	public int getPosition() {
		return pos;
	}

}
