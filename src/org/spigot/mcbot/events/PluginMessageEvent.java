package org.spigot.mcbot.events;

public class PluginMessageEvent extends CancellableEvent {
	private byte[] message;
	private String channel;

	public PluginMessageEvent(String channel, byte[] message) {
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
