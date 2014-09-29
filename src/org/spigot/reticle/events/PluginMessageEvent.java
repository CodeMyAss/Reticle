package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class PluginMessageEvent extends CancellableEvent {
	private final byte[] message;
	private final String messagestr;
	private final String channel;

	public PluginMessageEvent(mcbot bot,String channel, byte[] message) {
		super(bot);
		this.channel = channel;
		this.message = message;
		this.messagestr=new String(message);
	}
	
	public String getMessageAsString() {
		return messagestr;
	}

	public byte[] getMessage() {
		return message;
	}
	
	public String getChannel() {
		return channel;
	}
}
