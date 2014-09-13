package org.spigot.reticle.events;

public class PluginMessageSendEvent extends PluginMessageEvent {

	public PluginMessageSendEvent(String channel, byte[] message) {
		super(channel, message);
	}

}
