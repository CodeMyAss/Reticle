package org.spigot.reticle.events;

public class PluginMessageReceiveEvent extends PluginMessageEvent {

	public PluginMessageReceiveEvent(String channel, byte[] message) {
		super(channel, message);
	}

}
