package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.connector;

public class ChatEvent extends CancellableEvent {
	private String message;
	private int pos = 0;

	public ChatEvent(mcbot bot, String message) {
		super(bot);
		this.message = message;
	}

	public ChatEvent(mcbot bot, String message, int pos) {
		super(bot);
		this.message = message;
		this.pos = pos;
	}

	/**
	 * Returns message as it is received from server
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns formated message
	 * @return
	 */
	public String getFormatedMessage() {
		return connector.parsechat(message);
	}

	/**
	 * Returns chat position
	 * 
	 * @return
	 */
	public int getPosition() {
		return pos;
	}

}
