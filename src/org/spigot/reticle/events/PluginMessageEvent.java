package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class PluginMessageEvent extends CancellableEvent {
	private byte[] message;
	private String channel;

	public PluginMessageEvent(mcbot bot,String channel, byte[] message) {
		super(bot);
		this.channel = channel;
		this.message = message;
	}

	public byte[] getMessage() {
		return message;
	}
	
	public String getChannel() {
		return channel;
	}

	public String getMessageAsString() {
		return new String(message);
	}

}
